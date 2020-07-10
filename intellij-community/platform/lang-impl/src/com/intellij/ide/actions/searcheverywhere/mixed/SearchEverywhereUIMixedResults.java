// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ide.actions.searcheverywhere.mixed;

import com.google.common.collect.Lists;
import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.SearchTopHitProvider;
import com.intellij.ide.actions.SearchEverywhereClassifier;
import com.intellij.ide.actions.searcheverywhere.*;
import com.intellij.ide.actions.searcheverywhere.statistics.SearchEverywhereUsageTriggerCollector;
import com.intellij.ide.actions.searcheverywhere.statistics.SearchFieldStatisticsCollector;
import com.intellij.ide.util.gotoByName.GotoActionModel;
import com.intellij.ide.util.gotoByName.QuickSearchComponent;
import com.intellij.ide.util.gotoByName.SearchEverywhereConfiguration;
import com.intellij.internal.statistic.eventLog.FeatureUsageData;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.actionSystem.impl.ActionMenu;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.progress.util.ProgressWindow;
import com.intellij.openapi.progress.util.TooManyUsagesStatus;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.ui.popup.PopupUpdateProcessor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.*;
import com.intellij.usages.impl.UsageViewManagerImpl;
import com.intellij.util.Alarm;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Consumer;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.diff.Diff;
import com.intellij.util.diff.FilesTooBigForDiffException;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.text.MatcherHolder;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Konstantin Bulenkov
 * @author Mikhail.Sokolov
 */
public final class SearchEverywhereUIMixedResults extends SearchEverywhereUIBase implements DataProvider, QuickSearchComponent {
  private static final Logger LOG = Logger.getInstance(SearchEverywhereUIMixedResults.class);

  public static final int SINGLE_CONTRIBUTOR_ELEMENTS_LIMIT = 30;
  public static final int MULTIPLE_CONTRIBUTORS_ELEMENTS_LIMIT = 15;
  public static final int THROTTLING_TIMEOUT = 100;

  private static final SimpleTextAttributes SMALL_LABEL_ATTRS = new SimpleTextAttributes(
    SimpleTextAttributes.STYLE_SMALLER, JBUI.CurrentTheme.BigPopup.listTitleLabelForeground());

  private final List<? extends SearchEverywhereContributor<?>> myShownContributors;

  private SearchListModel myListModel;

  private SETab mySelectedTab;
  private final List<SETab> myTabs = new ArrayList<>();
  private final Function<String, String> myShortcutSupplier;

  private boolean myEverywhereAutoSet = true;
  private String myNotFoundString;

  private JBPopup myHint;

  private final SESearcher mySearcher;
  private final ThrottlingListenerWrapper myBufferedListener;
  private ProgressIndicator mySearchProgressIndicator;

  private final SEListSelectionTracker mySelectionTracker;
  private final PersistentSearchEverywhereContributorFilter<String> myContributorsFilter;
  private ActionToolbar myToolbar;

  public SearchEverywhereUIMixedResults(@Nullable Project project,
                            @NotNull List<? extends SearchEverywhereContributor<?>> contributors) {
    this(project, contributors, s -> null);
  }

  public SearchEverywhereUIMixedResults(@Nullable Project project,
                            @NotNull List<? extends SearchEverywhereContributor<?>> contributors,
                            @NotNull Function<String, String> shortcutSupplier) {
    super(project);
    List<SEResultsEqualityProvider> equalityProviders = SEResultsEqualityProvider.getProviders();
    myBufferedListener = new ThrottlingListenerWrapper(THROTTLING_TIMEOUT, mySearchListener, Runnable::run);
    mySearcher = new MultiThreadSearcher(myBufferedListener, run ->
      ApplicationManager.getApplication().invokeLater(run), equalityProviders);
    myShownContributors = contributors;
    myShortcutSupplier = shortcutSupplier;
    Map<String, String> namesMap = ContainerUtil.map2Map(contributors, c -> Pair.create(c.getSearchProviderId(), c.getFullGroupName()));
    myContributorsFilter = isAllTabNeeded()
                           ? new PersistentSearchEverywhereContributorFilter<>(
                                ContainerUtil.map(contributors, c -> c.getSearchProviderId()),
                                SearchEverywhereConfiguration.getInstance(project),
                                namesMap::get, c -> null)
                           : null;

    init();

    initSearchActions();

    myResultsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    myResultsList.addListSelectionListener(e -> {
      int[] selectedIndices = myResultsList.getSelectedIndices();
      if (selectedIndices.length > 1) {
        boolean multiSelection = Arrays.stream(selectedIndices)
          .allMatch(i -> myListModel.getContributorForIndex(i).isMultiSelectionSupported());
        if (!multiSelection) {
          int index = myResultsList.getLeadSelectionIndex();
          myResultsList.setSelectedIndex(index);
        }
      }
    });

    mySelectionTracker = new SEListSelectionTracker(myResultsList, myListModel);
    myResultsList.addListSelectionListener(mySelectionTracker);

    Disposer.register(this, SearchFieldStatisticsCollector.createAndStart(mySearchField, myProject));
  }

  @Override
  @NotNull
  protected CompositeCellRenderer createCellRenderer() {
    return new CompositeCellRenderer();
  }

  @NotNull
  @Override
  public JBList<Object> createList() {
    myListModel = new SearchListModel();
    Comparator<SearchEverywhereFoundElementInfo> commandsComparator = (element1, element2) -> {
      if (element1.getContributor() == myStubCommandContributor && element2.getContributor() == myStubCommandContributor) {
        SearchEverywhereCommandInfo command1 = (SearchEverywhereCommandInfo)element1.getElement();
        SearchEverywhereCommandInfo command2 = (SearchEverywhereCommandInfo)element2.getElement();

        return command1.getCommand().compareTo(command2.getCommand());
      }

      return element1.getContributor() == myStubCommandContributor ? 1
             : element2.getContributor() == myStubCommandContributor ? -1 : 0;
    };

    Comparator<SearchEverywhereFoundElementInfo> recentFilesComparator = (element1, element2) -> {
      boolean firstIsRecent = element1.getContributor() instanceof RecentFilesSEContributor;
      boolean secondIsRecent = element2.getContributor() instanceof RecentFilesSEContributor;

      if (firstIsRecent && secondIsRecent) {
        return Integer.compare(element1.getPriority(), element2.getPriority());
      }

      if (!firstIsRecent && !secondIsRecent) {
        return 0;
      }

      return firstIsRecent ? 1 : -1;
    };

    Comparator<SearchEverywhereFoundElementInfo> comparator = commandsComparator;
    if (Registry.is("search.everywhere.recent.at.top")) comparator = comparator.thenComparing(recentFilesComparator);
    comparator = comparator.thenComparing(SearchEverywhereFoundElementInfo.COMPARATOR).reversed();
    myListModel.setElementsComparator(comparator);

    addListDataListener(myListModel);

    return new JBList<>(myListModel);
  }

  @Override
  public void toggleEverywhereFilter() {
    myEverywhereAutoSet = false;
    if (mySelectedTab.everywhereAction == null) return;
    if (!mySelectedTab.everywhereAction.canToggleEverywhere()) return;
    mySelectedTab.everywhereAction.setEverywhere(
      !mySelectedTab.everywhereAction.isEverywhere());
    myToolbar.updateActionsImmediately();
  }

  private boolean isAllTabNeeded() {
    return myShownContributors.size() > 1;
  }

  private void setEverywhereAuto(boolean everywhere) {
    myEverywhereAutoSet = true;
    if (mySelectedTab.everywhereAction == null) return;
    if (!mySelectedTab.everywhereAction.canToggleEverywhere()) return;
    mySelectedTab.everywhereAction.setEverywhere(everywhere);
    myToolbar.updateActionsImmediately();
  }

  private boolean isEverywhere() {
    if (mySelectedTab.everywhereAction == null) return true;
    return mySelectedTab.everywhereAction.isEverywhere();
  }

  private boolean canToggleEverywhere() {
    if (mySelectedTab.everywhereAction == null) return false;
    return mySelectedTab.everywhereAction.canToggleEverywhere();
  }

  @Override
  public void switchToContributor(@NotNull String contributorID) {
    SETab selectedTab = myTabs.stream()
      .filter(tab -> tab.getID().equals(contributorID))
      .findAny()
      .orElseThrow(() -> new IllegalArgumentException(String.format("Contributor %s is not supported", contributorID)));
    switchToTab(selectedTab);
  }

  private void switchToNextTab() {
    int currentIndex = myTabs.indexOf(mySelectedTab);
    SETab nextTab = currentIndex == myTabs.size() - 1 ? myTabs.get(0) : myTabs.get(currentIndex + 1);
    switchToTab(nextTab);
  }

  private void switchToPrevTab() {
    int currentIndex = myTabs.indexOf(mySelectedTab);
    SETab prevTab = currentIndex == 0 ? myTabs.get(myTabs.size() - 1) : myTabs.get(currentIndex - 1);
    switchToTab(prevTab);
  }

  private void switchToTab(SETab tab) {
    boolean prevTabIsAll = mySelectedTab != null && isAllTabSelected();
    mySelectedTab = tab;
    boolean nextTabIsAll = isAllTabSelected();

    if (myEverywhereAutoSet && isEverywhere() && canToggleEverywhere()) {
      setEverywhereAuto(false);
    }

    updateSearchFieldAdvertisement();

    if (prevTabIsAll != nextTabIsAll) {
      //reset cell renderer to show/hide group titles in "All" tab
      myResultsList.setCellRenderer(myResultsList.getCellRenderer());
    }
    if (myToolbar != null) {
      myToolbar.updateActionsImmediately();
    }
    repaint();
    scheduleRebuildList();
  }

  private final JLabel myAdvertisementLabel = new JBLabel();
  {
    myAdvertisementLabel.setForeground(JBUI.CurrentTheme.BigPopup.searchFieldGrayForeground());
    myAdvertisementLabel.setFont(RelativeFont.SMALL.derive(getFont()));
  }
  private void updateSearchFieldAdvertisement() {
    if (mySearchField == null) return;

    Boolean commandsSupported = mySelectedTab.getContributor()
      .map(contributor -> !contributor.getSupportedCommands().isEmpty())
      .orElse(true);

    String advertisementText;
    if (commandsSupported) {
      advertisementText = IdeBundle.message("searcheverywhere.textfield.hint", SearchTopHitProvider.getTopHitAccelerator());
    }
    else {
      advertisementText = mySelectedTab.getContributor().map(c -> c.getAdvertisement()).orElse(null);
    }

    mySearchField.remove(myAdvertisementLabel);
    if (advertisementText != null) {
      myAdvertisementLabel.setText(advertisementText);
      mySearchField.add(myAdvertisementLabel, BorderLayout.EAST);
    }
  }

  @Override
  public String getSelectedContributorID() {
    return mySelectedTab.getID();
  }

  @Override
  @Nullable
  public Object getSelectionIdentity() {
    Object value = myResultsList.getSelectedValue();
    return value == null ? null : Objects.hashCode(value);
  }

  @Override
  public void dispose() {
    stopSearching();
    myListModel.clear();
  }

  @Nullable
  @Override
  public Object getData(@NotNull String dataId) {
    IntStream indicesStream = Arrays.stream(myResultsList.getSelectedIndices())
      .filter(i -> !myListModel.isMoreElement(i));

    //common data section---------------------
    if (PlatformDataKeys.PREDEFINED_TEXT.is(dataId)) {
      return getSearchPattern();
    }
    if (CommonDataKeys.PROJECT.is(dataId)) {
      return myProject;
    }

    if (LangDataKeys.PSI_ELEMENT_ARRAY.is(dataId)) {
      List<PsiElement> elements = indicesStream.mapToObj(i -> {
        SearchEverywhereContributor<Object> contributor = myListModel.getContributorForIndex(i);
        Object item = myListModel.getElementAt(i);
        Object psi = contributor.getDataForItem(item, CommonDataKeys.PSI_ELEMENT.getName());
        return (PsiElement)psi;
      })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
      return PsiUtilCore.toPsiElementArray(elements);
    }

    //item-specific data section--------------
    return indicesStream.mapToObj(i -> {
      SearchEverywhereContributor<Object> contributor = myListModel.getContributorForIndex(i);
      Object item = myListModel.getElementAt(i);
      return contributor.getDataForItem(item, dataId);
    })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  @Override
  public void registerHint(@NotNull JBPopup h) {
    if (myHint != null && myHint.isVisible() && myHint != h) {
      myHint.cancel();
    }
    myHint = h;
  }

  @Override
  public void unregisterHint() {
    myHint = null;
  }

  private void hideHint() {
    if (myHint != null && myHint.isVisible()) {
      myHint.cancel();
    }
  }

  private void updateHint(Object element) {
    if (myHint == null || !myHint.isVisible()) return;
    final PopupUpdateProcessor updateProcessor = myHint.getUserData(PopupUpdateProcessor.class);
    if (updateProcessor != null) {
      updateProcessor.updatePopup(element);
    }
  }

  private boolean isAllTabSelected() {
    return SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID.equals(getSelectedContributorID());
  }

  @Override
  @NotNull
  protected JPanel createSettingsPanel() {
    DefaultActionGroup actionGroup = new DefaultActionGroup();
    actionGroup.addAction(new ActionGroup() {
      @Override
      public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        if (e == null || mySelectedTab == null) return EMPTY_ARRAY;
        return mySelectedTab.actions.toArray(EMPTY_ARRAY);
      }
    });

    if (myProject != null) {
      actionGroup.addAction(new ShowInFindToolWindowAction());
    }

    myToolbar = ActionManager.getInstance().createActionToolbar("search.everywhere.toolbar", actionGroup, true);
    myToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
    myToolbar.updateActionsImmediately();
    JComponent toolbarComponent = myToolbar.getComponent();
    toolbarComponent.setOpaque(false);
    toolbarComponent.setBorder(JBUI.Borders.empty(2, 18, 2, 9));
    return (JPanel)toolbarComponent;
  }

  @NotNull
  @Override
  protected String getInitialHint() {
    return IdeBundle.message("searcheverywhere.history.shortcuts.hint",
                             KeymapUtil.getKeystrokeText(SearchTextField.ALT_SHOW_HISTORY_KEYSTROKE),
                             KeymapUtil.getKeystrokeText(SearchTextField.SHOW_HISTORY_KEYSTROKE));
  }

  @Override
  protected @NotNull String getAccessibleName() {
    return IdeBundle.message("searcheverywhere.accessible.name");
  }

  @NotNull
  @Override
  protected ExtendableTextField createSearchField() {
    SearchField res = new SearchField() {
      @NotNull
      @Override
      protected Extension getLeftExtension() {
        return new Extension() {
          @Override
          public Icon getIcon(boolean hovered) {
            return AllIcons.Actions.Search;
          }

          @Override
          public boolean isIconBeforeText() {
            return true;
          }

          @Override
          public int getIconGap() {
            return JBUIScale.scale(10);
          }
        };
      }
    };
    res.putClientProperty(SEARCH_EVERYWHERE_SEARCH_FILED_KEY, true);
    res.setLayout(new BorderLayout());
    return res;
  }

  @Override
  protected void installScrollingActions() {
    ScrollingUtil.installMoveUpAction(myResultsList, getSearchField());
    ScrollingUtil.installMoveDownAction(myResultsList, getSearchField());
  }

  @Override
  @NotNull
  protected JPanel createTopLeftPanel() {
    JPanel contributorsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    contributorsPanel.setOpaque(false);

    if (isAllTabNeeded()) {
      SETab allTab = new SETab(null);
      contributorsPanel.add(allTab);
      myTabs.add(allTab);
    }

    myShownContributors.stream()
      .filter(SearchEverywhereContributor::isShownInSeparateTab)
      .forEach(contributor -> {
        SETab tab = new SETab(contributor);
        contributorsPanel.add(tab);
        myTabs.add(tab);
      });

    return contributorsPanel;
  }

  private class SETab extends JLabel {
    final SearchEverywhereContributor<?> contributor;
    final List<AnAction> actions;
    final SearchEverywhereToggleAction everywhereAction;

    SETab(@Nullable SearchEverywhereContributor<?> contributor) {
      super(contributor == null ? IdeBundle.message("searcheverywhere.allelements.tab.name") : contributor.getGroupName());
      this.contributor = contributor;
      updateTooltip();
      Runnable onChanged = () -> {
        myToolbar.updateActionsImmediately();
        scheduleRebuildList();
      };
      if (contributor == null) {
        String actionText = IdeUICustomization.getInstance().projectMessage("checkbox.include.non.project.items");
        actions = Arrays.asList(new CheckBoxSearchEverywhereToggleAction(actionText) {
          final SearchEverywhereManagerImpl seManager = (SearchEverywhereManagerImpl)SearchEverywhereManager.getInstance(myProject);
          @Override
          public boolean isEverywhere() {
            return seManager.isEverywhere();
          }

          @Override
          public void setEverywhere(boolean state) {
            seManager.setEverywhere(state);
            myTabs.stream()
              .filter(tab -> tab != SETab.this)
              .forEach(tab -> tab.everywhereAction.setEverywhere(state));
            onChanged.run();
          }
        }, new FiltersAction(myContributorsFilter, onChanged));
      }
      else {
        actions = new ArrayList<>(contributor.getActions(onChanged));
      }
      everywhereAction = (SearchEverywhereToggleAction)ContainerUtil.find(actions, o -> o instanceof SearchEverywhereToggleAction);
      Insets insets = JBUI.CurrentTheme.BigPopup.tabInsets();
      setBorder(JBUI.Borders.empty(insets.top, insets.left, insets.bottom, insets.right));
      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          switchToTab(SETab.this);
          String reportableID = getContributor()
            .map(SearchEverywhereUsageTriggerCollector::getReportableContributorID)
            .orElse(SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID);
          FeatureUsageData data = SearchEverywhereUsageTriggerCollector
            .createData(reportableID)
            .addInputEvent(e);
          featureTriggered(SearchEverywhereUsageTriggerCollector.TAB_SWITCHED, data);
        }
      });
    }

    private void updateTooltip() {
      String shortcut = myShortcutSupplier.apply(getID());
      if (shortcut != null) {
        setToolTipText(shortcut);
      }
    }

    public String getID() {
      return getContributor()
        .map(SearchEverywhereContributor::getSearchProviderId)
        .orElse(SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID);
    }

    public Optional<SearchEverywhereContributor<?>> getContributor() {
      return Optional.ofNullable(contributor);
    }

    @Override
    public Dimension getPreferredSize() {
      Dimension size = super.getPreferredSize();
      size.height = JBUIScale.scale(29);
      return size;
    }

    @Override
    public boolean isOpaque() {
      return mySelectedTab == this;
    }

    @Override
    public Color getBackground() {
      return mySelectedTab == this
             ? JBUI.CurrentTheme.BigPopup.selectedTabColor()
             : super.getBackground();
    }

    @Override
    public Color getForeground() {
      return mySelectedTab == this
             ? JBUI.CurrentTheme.BigPopup.selectedTabTextColor()
             : super.getForeground();
    }
  }

  private static final long REBUILD_LIST_DELAY = 100;
  private final Alarm rebuildListAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);

  private void scheduleRebuildList() {
    if (rebuildListAlarm.getActiveRequestCount() == 0) rebuildListAlarm.addRequest(() -> rebuildList(), REBUILD_LIST_DELAY);
  }

  private void rebuildList() {
    ApplicationManager.getApplication().assertIsDispatchThread();

    stopSearching();

    myResultsList.setEmptyText(IdeBundle.message("label.choosebyname.searching"));
    String rawPattern = getSearchPattern();
    updateViewType(rawPattern.isEmpty() ? ViewType.SHORT : ViewType.FULL);
    String namePattern = mySelectedTab.getContributor()
      .map(contributor -> contributor.filterControlSymbols(rawPattern))
      .orElse(rawPattern);

    MinusculeMatcher matcher =
      NameUtil.buildMatcherWithFallback("*" + rawPattern, "*" + namePattern, NameUtil.MatchingCaseSensitivity.NONE);
    MatcherHolder.associateMatcher(myResultsList, matcher);

    Map<SearchEverywhereContributor<?>, Integer> contributorsMap = new HashMap<>();
    Optional<SearchEverywhereContributor<?>> selectedContributor = mySelectedTab.getContributor();
    if (selectedContributor.isPresent()) {
      contributorsMap.put(selectedContributor.get(), SINGLE_CONTRIBUTOR_ELEMENTS_LIMIT);
    }
    else {
      contributorsMap.putAll(getAllTabContributors().stream().collect(Collectors.toMap(c -> c, c -> MULTIPLE_CONTRIBUTORS_ELEMENTS_LIMIT)));
    }

    List<SearchEverywhereContributor<?>> contributors;
    if (myProject != null) {
      contributors = DumbService.getInstance(myProject).filterByDumbAwareness(contributorsMap.keySet());
      if (contributors.isEmpty() && DumbService.isDumb(myProject)) {
        myResultsList.setEmptyText(IdeBundle.message("searcheverywhere.indexing.mode.not.supported",
                                                     mySelectedTab.getText(),
                                                     ApplicationNamesInfo.getInstance().getFullProductName()));
        myListModel.clear();
        return;
      }
      if (contributors.size() != contributorsMap.size()) {
        myResultsList.setEmptyText(IdeBundle.message("searcheverywhere.indexing.incomplete.results",
                                                     mySelectedTab.getText(),
                                                     ApplicationNamesInfo.getInstance().getFullProductName()));
      }
    }
    else {
      contributors = new ArrayList<>(contributorsMap.keySet());
    }

    myListModel.expireResults();
    contributors.forEach(contributor -> myListModel.setHasMore(contributor, false));
    String commandPrefix = SearchTopHitProvider.getTopHitAccelerator();
    if (rawPattern.startsWith(commandPrefix)) {
      String typedCommand = rawPattern.split(" ")[0].substring(commandPrefix.length());
      List<SearchEverywhereCommandInfo> commands = getCommandsForCompletion(contributors, typedCommand);

      if (!commands.isEmpty()) {
        if (rawPattern.contains(" ")) {
          contributorsMap.keySet().retainAll(commands.stream()
                                               .map(SearchEverywhereCommandInfo::getContributor)
                                               .collect(Collectors.toSet()));
        }
        else {
          myListModel.clear();
          List<SearchEverywhereFoundElementInfo> lst = ContainerUtil.map(
            commands, command -> new SearchEverywhereFoundElementInfo(command, 0, myStubCommandContributor));
          myListModel.addElements(lst);
          ScrollingUtil.ensureSelectionExists(myResultsList);
        }
      }
    }
    mySearchProgressIndicator = mySearcher.search(contributorsMap, rawPattern);
  }

  private void initSearchActions() {
    MouseAdapter listMouseListener = new MouseAdapter() {
      private int currentDescriptionIndex = -1;

      @Override
      public void mouseClicked(MouseEvent e) {
        onMouseClicked(e);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        int index = myResultsList.locationToIndex(e.getPoint());
        indexChanged(index);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        int index = myResultsList.getSelectedIndex();
        indexChanged(index);
      }

      private void indexChanged(int index) {
        if (index != currentDescriptionIndex) {
          currentDescriptionIndex = index;
          showDescriptionForIndex(index);
        }
      }
    };
    myResultsList.addMouseMotionListener(listMouseListener);
    myResultsList.addMouseListener(listMouseListener);

    ScrollingUtil.redirectExpandSelection(myResultsList, mySearchField);

    Consumer<AnActionEvent> nextTabAction = e -> {
      switchToNextTab();
      triggerTabSwitched(e);
    };
    Consumer<AnActionEvent> prevTabAction = e -> {
      switchToPrevTab();
      triggerTabSwitched(e);
    };

    registerAction(SearchEverywhereActions.AUTOCOMPLETE_COMMAND, CompleteCommandAction::new);
    registerAction(SearchEverywhereActions.SWITCH_TO_NEXT_TAB, nextTabAction);
    registerAction(SearchEverywhereActions.SWITCH_TO_PREV_TAB, prevTabAction);
    registerAction(IdeActions.ACTION_NEXT_TAB, nextTabAction);
    registerAction(IdeActions.ACTION_PREVIOUS_TAB, prevTabAction);
    registerAction(IdeActions.ACTION_SWITCHER, e -> {
      if (e.getInputEvent().isShiftDown()) {
        switchToPrevTab();
      }
      else {
        switchToNextTab();
      }
      triggerTabSwitched(e);
    });
    registerAction(SearchEverywhereActions.NAVIGATE_TO_NEXT_GROUP, e -> {
      scrollList(true);
      FeatureUsageData data = SearchEverywhereUsageTriggerCollector
        .createData(null)
        .addInputEvent(e);
      featureTriggered(SearchEverywhereUsageTriggerCollector.GROUP_NAVIGATE, data);
    });
    registerAction(SearchEverywhereActions.NAVIGATE_TO_PREV_GROUP, e -> {
      scrollList(false);
      FeatureUsageData data = SearchEverywhereUsageTriggerCollector
        .createData(null)
        .addInputEvent(e);
      featureTriggered(SearchEverywhereUsageTriggerCollector.GROUP_NAVIGATE, data);
    });
    registerSelectItemAction();

    AnAction escape = ActionManager.getInstance().getAction("EditorEscape");
    DumbAwareAction.create(__ -> closePopup())
      .registerCustomShortcutSet(escape == null ? CommonShortcuts.ESCAPE : escape.getShortcutSet(), this);

    mySearchField.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        String newSearchString = getSearchPattern();
        if (myNotFoundString != null) {
          boolean newPatternContainsPrevious = myNotFoundString.length() > 1 && newSearchString.contains(myNotFoundString);
          if (myEverywhereAutoSet && isEverywhere() && canToggleEverywhere() && !newPatternContainsPrevious) {
            myNotFoundString = null;
            setEverywhereAuto(false);
            return;
          }
        }

        scheduleRebuildList();
      }
    });

    myResultsList.addListSelectionListener(e -> {
      Object selectedValue = myResultsList.getSelectedValue();
      if (selectedValue != null && myHint != null && myHint.isVisible()) {
        updateHint(selectedValue);
      }

      showDescriptionForIndex(myResultsList.getSelectedIndex());
    });

    MessageBusConnection busConnection = myProject != null
                                         ? myProject.getMessageBus().connect(this)
                                         : ApplicationManager.getApplication().getMessageBus().connect(this);

    busConnection.subscribe(DumbService.DUMB_MODE, new DumbService.DumbModeListener() {
      @Override
      public void exitDumbMode() {
        ApplicationManager.getApplication().invokeLater(() -> {
          updateSearchFieldAdvertisement();
          scheduleRebuildList();
        });
      }
    });
    busConnection.subscribe(AnActionListener.TOPIC, new AnActionListener() {
      @Override
      public void afterActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, @NotNull AnActionEvent event) {
        if (action == mySelectedTab.everywhereAction && event.getInputEvent() != null) {
          myEverywhereAutoSet = false;
        }
      }
    });

    busConnection.subscribe(ProgressWindow.TOPIC, pw -> Disposer.register(pw, () -> myResultsList.repaint()));

    mySearchField.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        Component oppositeComponent = e.getOppositeComponent();
        if (!isHintComponent(oppositeComponent) && !UIUtil.haveCommonOwner(SearchEverywhereUIMixedResults.this, oppositeComponent)) {
          closePopup();
        }
      }
    });
  }

  private void showDescriptionForIndex(int index) {
    if (index >= 0 && !myListModel.isMoreElement(index)) {
      SearchEverywhereContributor<Object> contributor = myListModel.getContributorForIndex(index);
      Object data = contributor.getDataForItem(
        myListModel.getElementAt(index), SearchEverywhereDataKeys.ITEM_STRING_DESCRIPTION.getName());
      if (data instanceof String) {
        ActionMenu.showDescriptionInStatusBar(true, myResultsList, (String)data);
      }
    }
  }

  private void registerAction(String actionID, Supplier<? extends AnAction> actionSupplier) {
    Optional.ofNullable(ActionManager.getInstance().getAction(actionID))
      .map(a -> a.getShortcutSet())
      .ifPresent(shortcuts -> actionSupplier.get().registerCustomShortcutSet(shortcuts, this, this));
  }

  private void registerAction(String actionID, Consumer<? super AnActionEvent> action) {
    registerAction(actionID, () -> DumbAwareAction.create(action));
  }

  // when user adds shortcut for "select item" we should add shortcuts
  // with all possible modifiers (Ctrl, Shift, Alt, etc.)
  private void registerSelectItemAction() {
    int[] allowedModifiers = new int[]{
      0,
      InputEvent.SHIFT_MASK,
      InputEvent.CTRL_MASK,
      InputEvent.META_MASK,
      InputEvent.ALT_MASK
    };

    ShortcutSet selectShortcuts = ActionManager.getInstance().getAction(SearchEverywhereActions.SELECT_ITEM).getShortcutSet();
    Collection<KeyboardShortcut> keyboardShortcuts = Arrays.stream(selectShortcuts.getShortcuts())
      .filter(shortcut -> shortcut instanceof KeyboardShortcut)
      .map(shortcut -> (KeyboardShortcut)shortcut)
      .collect(Collectors.toList());

    for (int modifiers : allowedModifiers) {
      Collection<Shortcut> newShortcuts = new ArrayList<>();
      for (KeyboardShortcut shortcut : keyboardShortcuts) {
        boolean hasSecondStroke = shortcut.getSecondKeyStroke() != null;
        KeyStroke originalStroke = hasSecondStroke ? shortcut.getSecondKeyStroke() : shortcut.getFirstKeyStroke();

        if ((originalStroke.getModifiers() & modifiers) != 0) continue;

        KeyStroke newStroke = KeyStroke.getKeyStroke(originalStroke.getKeyCode(), originalStroke.getModifiers() | modifiers);
        newShortcuts.add(hasSecondStroke
                         ? new KeyboardShortcut(shortcut.getFirstKeyStroke(), newStroke)
                         : new KeyboardShortcut(newStroke, null));
      }
      if (newShortcuts.isEmpty()) continue;

      ShortcutSet newShortcutSet = new CustomShortcutSet(newShortcuts.toArray(Shortcut.EMPTY_ARRAY));
      DumbAwareAction.create(event -> {
        int[] indices = myResultsList.getSelectedIndices();
        elementsSelected(indices, modifiers);
      }).registerCustomShortcutSet(newShortcutSet, this, this);
    }
  }

  private void triggerTabSwitched(AnActionEvent e) {
    String id = mySelectedTab.getContributor()
      .map(SearchEverywhereUsageTriggerCollector::getReportableContributorID)
      .orElse(SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID);

    FeatureUsageData data = SearchEverywhereUsageTriggerCollector
      .createData(id)
      .addInputEvent(e);
    featureTriggered(SearchEverywhereUsageTriggerCollector.TAB_SWITCHED, data);
  }

  private void scrollList(boolean down) {
    int newIndex = down ? myListModel.getSize() - 1 : 0;
    int currentIndex = myResultsList.getSelectedIndex();

    if (newIndex != currentIndex) {
      myResultsList.setSelectedIndex(newIndex);
      ScrollingUtil.ensureIndexIsVisible(myResultsList, newIndex, 0);
    }
  }

  private Optional<SearchEverywhereCommandInfo> getSelectedCommand(String typedCommand) {
    int index = myResultsList.getSelectedIndex();
    if (index < 0) return Optional.empty();

    SearchEverywhereContributor contributor = myListModel.getContributorForIndex(index);
    if (contributor != myStubCommandContributor) return Optional.empty();

    SearchEverywhereCommandInfo selectedCommand = (SearchEverywhereCommandInfo)myListModel.getElementAt(index);
    return selectedCommand.getCommand().contains(typedCommand) ? Optional.of(selectedCommand) : Optional.empty();
  }

  @NotNull
  private static List<SearchEverywhereCommandInfo> getCommandsForCompletion(Collection<? extends SearchEverywhereContributor<?>> contributors,
                                                                            String enteredCommandPart) {
    Comparator<SearchEverywhereCommandInfo> cmdComparator = (cmd1, cmd2) -> {
      String cmdName1 = cmd1.getCommand();
      String cmdName2 = cmd2.getCommand();
      if (!enteredCommandPart.isEmpty()) {
        if (cmdName1.startsWith(enteredCommandPart) && !cmdName2.startsWith(enteredCommandPart)) return -1;
        if (!cmdName1.startsWith(enteredCommandPart) && cmdName2.startsWith(enteredCommandPart)) return 1;
      }

      return String.CASE_INSENSITIVE_ORDER.compare(cmdName1, cmd2.getCommand());
    };

    return contributors.stream()
      .flatMap(contributor -> contributor.getSupportedCommands().stream())
      .filter(command -> command.getCommand().contains(enteredCommandPart))
      .sorted(cmdComparator)
      .collect(Collectors.toList());
  }

  private void onMouseClicked(@NotNull MouseEvent e) {
    boolean multiSelectMode = e.isShiftDown() || UIUtil.isControlKeyDown(e);
    if (e.getButton() == MouseEvent.BUTTON1 && !multiSelectMode) {
      e.consume();
      final int i = myResultsList.locationToIndex(e.getPoint());
      if (i > -1) {
        myResultsList.setSelectedIndex(i);
        elementsSelected(new int[]{i}, e.getModifiers());
      }
    }
  }

  private boolean isHintComponent(Component component) {
    if (myHint != null && !myHint.isDisposed() && component != null) {
      return SwingUtilities.isDescendingFrom(component, myHint.getContent());
    }
    return false;
  }

  private void elementsSelected(int[] indexes, int modifiers) {
    if (indexes.length == 1 && myListModel.isMoreElement(indexes[0])) {
      showMoreElements();
      return;
    }

    indexes = Arrays.stream(indexes)
      .filter(i -> !myListModel.isMoreElement(i))
      .toArray();

    String searchText = getSearchPattern();
    if (searchText.startsWith(SearchTopHitProvider.getTopHitAccelerator()) && searchText.contains(" ")) {
      featureTriggered(SearchEverywhereUsageTriggerCollector.COMMAND_USED, null);
    }

    boolean closePopup = false;
    for (int i : indexes) {
      SearchEverywhereContributor<Object> contributor = myListModel.getContributorForIndex(i);
      Object value = myListModel.getElementAt(i);

      String selectedTabContributorID = mySelectedTab.getContributor()
        .map(SearchEverywhereUsageTriggerCollector::getReportableContributorID)
        .orElse(SearchEverywhereManagerImpl.ALL_CONTRIBUTORS_GROUP_ID);

      String reportableContributorID = SearchEverywhereUsageTriggerCollector.getReportableContributorID(contributor);
      FeatureUsageData data = SearchEverywhereUsageTriggerCollector.createData(reportableContributorID, selectedTabContributorID, i);
      if (value instanceof PsiElement) {
        data.addLanguage(((PsiElement) value).getLanguage());
      }
      featureTriggered(SearchEverywhereUsageTriggerCollector.CONTRIBUTOR_ITEM_SELECTED, data);

      closePopup |= contributor.processSelectedItem(value, modifiers, searchText);
    }

    if (closePopup) {
      closePopup();
    }
    else {
      ApplicationManager.getApplication().invokeLater(() -> myResultsList.repaint());
    }
  }

  private void showMoreElements() {
    featureTriggered(SearchEverywhereUsageTriggerCollector.MORE_ITEM_SELECTED, null);

    myListModel.clearMoreItem();
    Map<SearchEverywhereContributor<?>, Collection<SearchEverywhereFoundElementInfo>> found = myListModel.getFoundElementsMap();
    int limitAdditional = isAllTabSelected() ? MULTIPLE_CONTRIBUTORS_ELEMENTS_LIMIT : SINGLE_CONTRIBUTOR_ELEMENTS_LIMIT;
    Map<SearchEverywhereContributor<?>, Integer> contributorsAndLimits = found.entrySet().stream()
      .filter(entry -> myListModel.hasMoreElements(entry.getKey()))
      .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().size() + limitAdditional));

    myListModel.setMaxFrozenIndex(myListModel.getSize() - 1);
    mySearchProgressIndicator = mySearcher.findMoreItems(found, contributorsAndLimits, getSearchPattern());
  }

  private void stopSearching() {
    if (mySearchProgressIndicator != null && !mySearchProgressIndicator.isCanceled()) {
      mySearchProgressIndicator.cancel();
    }
    if (myBufferedListener != null) {
      myBufferedListener.clearBuffer();
    }
  }

  private void closePopup() {
    ActionMenu.showDescriptionInStatusBar(true, myResultsList, null);
    stopSearching();
    searchFinishedHandler.run();
  }

  @NotNull
  private List<SearchEverywhereContributor<?>> getAllTabContributors() {
    return ContainerUtil.filter(myShownContributors, contributor -> myContributorsFilter.isSelected(contributor.getSearchProviderId()));
  }

  @NotNull
  private Collection<SearchEverywhereContributor<?>> getContributorsForCurrentTab() {
    return isAllTabSelected() ? getAllTabContributors() : Collections.singleton(mySelectedTab.getContributor().get());
  }

  @TestOnly
  public Future<List<Object>> findElementsForPattern(String pattern) {
    CompletableFuture<List<Object>> future = new CompletableFuture<>();
    mySearchListener.setTestCallback(list -> {
      future.complete(list);
      mySearchListener.setTestCallback(null);
    });
    mySearchField.setText(pattern);
    return future;
  }

  private class CompositeCellRenderer implements ListCellRenderer<Object> {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      if (value == SearchListModel.MORE_ELEMENT) {
        Component component = myMoreRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        component.setPreferredSize(UIUtil.updateListRowHeight(component.getPreferredSize()));
        return component;
      }

      SearchEverywhereContributor<Object> contributor = myListModel.getContributorForIndex(index);
      Component component = SearchEverywhereClassifier.EP_Manager.getListCellRendererComponent(
        list, value, index, isSelected, cellHasFocus);
      if (component == null) {
        component = contributor.getElementsRenderer().getListCellRendererComponent(
          list, value, index, isSelected, true);
      }

      if (component instanceof JComponent) {
        Border border = ((JComponent)component).getBorder();
        if (border != GotoActionModel.GotoActionListCellRenderer.TOGGLE_BUTTON_BORDER) {
          ((JComponent)component).setBorder(JBUI.Borders.empty(1, 2));
        }
      }
      AppUIUtil.targetToDevice(component, list);
      component.setPreferredSize(UIUtil.updateListRowHeight(component.getPreferredSize()));

      return component;
    }
  }

  private final ListCellRenderer<Object> myCommandRenderer = new ColoredListCellRenderer<Object>() {

    @Override
    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
      setPaintFocusBorder(false);
      setIcon(EmptyIcon.ICON_16);
      setFont(list.getFont());

      SearchEverywhereCommandInfo command = (SearchEverywhereCommandInfo)value;
      append(command.getCommandWithPrefix() + " ", new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.getForeground()));
      append(command.getDefinition(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GRAY));
      setBackground(UIUtil.getListBackground(selected));
    }
  };

  private final ListCellRenderer<Object> myMoreRenderer = new ColoredListCellRenderer<Object>() {

    @Override
    protected int getMinHeight() {
      return -1;
    }

    @Override
    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
      if (value != SearchListModel.MORE_ELEMENT) {
        throw new AssertionError(value);
      }
      setFont(UIUtil.getLabelFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL)));
      append(IdeBundle.message("search.everywhere.points.more"), SMALL_LABEL_ATTRS);
      setIpad(JBInsets.create(1, 7));
      setMyBorder(null);
    }
  };

  public static class SearchListModel extends AbstractListModel<Object> {

    static final Object MORE_ELEMENT = new Object();

    private final List<SearchEverywhereFoundElementInfo> listElements = new ArrayList<>();
    private final Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors = new HashMap<>();

    private boolean resultsExpired = false;
    private Comparator<SearchEverywhereFoundElementInfo> myElementsComparator = SearchEverywhereFoundElementInfo.COMPARATOR.reversed();

    // new elements cannot be added before this index when "more..." elements are loaded
    private int myMaxFrozenIndex;

    public void setElementsComparator(Comparator<SearchEverywhereFoundElementInfo> elementsComparator) {
      myElementsComparator = elementsComparator;
    }

    public void setMaxFrozenIndex(int maxFrozenIndex) {
      myMaxFrozenIndex = maxFrozenIndex;
    }

    public boolean isResultsExpired() {
      return resultsExpired;
    }

    public void expireResults() {
      resultsExpired = true;
    }

    @Override
    public int getSize() {
      return listElements.size();
    }

    @Override
    public Object getElementAt(int index) {
      return listElements.get(index).getElement();
    }

    public List<Object> getItems() {
      return new ArrayList<>(values());
    }

    public Collection<Object> getFoundItems(SearchEverywhereContributor contributor) {
      return listElements.stream()
        .filter(info -> info.getContributor() == contributor && info.getElement() != MORE_ELEMENT)
        .map(info -> info.getElement())
        .collect(Collectors.toList());
    }

    public boolean hasMoreElements(SearchEverywhereContributor<?> contributor) {
      return Boolean.TRUE.equals(hasMoreContributors.get(contributor));
    }

    public void addElements(List<? extends SearchEverywhereFoundElementInfo> items) {
      if (items.isEmpty()) {
        return;
      }

      items = items.stream()
        .sorted(myElementsComparator)
        .collect(Collectors.toList());

      if (resultsExpired) {
        clearMoreItem();

        Object[] oldItems = ArrayUtil.toObjectArray(getItems());
        Object[] newItems = items.stream()
          .map(SearchEverywhereFoundElementInfo::getElement)
          .toArray();
        try {
          Diff.Change change = Diff.buildChanges(oldItems, newItems);
          applyChange(change, items);
        }
        catch (FilesTooBigForDiffException e) {
          LOG.error("Cannot calculate diff for updated search results");
        }

        resultsExpired = false;
      }
      else {
        int startIndex = listElements.size();
        listElements.addAll(items);
        int endIndex = listElements.size() - 1;
        fireIntervalAdded(this, startIndex, endIndex);

        // there were items for this contributor before update
        if (startIndex > 0) {
          List<SearchEverywhereFoundElementInfo> lst = myMaxFrozenIndex >= 0
               ? listElements.subList(myMaxFrozenIndex + 1, listElements.size())
               : listElements;
          lst.sort(myElementsComparator);
          fireContentsChanged(this, 0, endIndex);
        }
      }
    }

    public void clearMoreItem() {
      if (listElements.isEmpty()) return;

      int lastItemIndex = listElements.size() - 1;
      SearchEverywhereFoundElementInfo lastItem = listElements.get(lastItemIndex);
      if (lastItem.getElement() == MORE_ELEMENT) {
        listElements.remove(lastItemIndex);
        fireIntervalRemoved(this, lastItemIndex, lastItemIndex);
      }
    }

    private void applyChange(Diff.Change change, List<? extends SearchEverywhereFoundElementInfo> newItems) {
      for (Diff.Change ch : toRevertedList(change)) {
        if (ch.deleted > 0) {
          listElements.subList(ch.line0, ch.line0 + ch.deleted).clear();
          fireIntervalRemoved(this, ch.line0, ch.line0 + ch.deleted - 1);
        }

        if (ch.inserted > 0) {
          List<? extends SearchEverywhereFoundElementInfo> addedItems = newItems.subList(ch.line1, ch.line1 + ch.inserted);
          listElements.addAll(ch.line0, addedItems);
          fireIntervalAdded(this, ch.line0, ch.line0 + ch.inserted - 1);
        }
      }
    }

    private static List<Diff.Change> toRevertedList(Diff.Change change) {
      List<Diff.Change> res = new ArrayList<>();
      while (change != null) {
        res.add(0, change);
        change = change.link;
      }
      return res;
    }

    public void removeElement(@NotNull Object item, SearchEverywhereContributor<?> contributor) {
      if (listElements.isEmpty()) return;

      for (int i = 0; i < listElements.size(); i++){
        SearchEverywhereFoundElementInfo info = listElements.get(i);
        if (info.getContributor() == contributor && info.getElement().equals(item)) {
          listElements.remove(i);
          fireIntervalRemoved(this, i, i);
          return;
        }
      }
    }

    public void setHasMore(SearchEverywhereContributor<?> contributor, boolean contributorHasMore) {
      hasMoreContributors.put(contributor, contributorHasMore);

      int lasItemIndex = listElements.size() - 1;
      if (lasItemIndex < 0) {
        return;
      }

      boolean hasMore = hasMoreContributors.values().stream().anyMatch(val -> val);
      boolean alreadyHas = isMoreElement(lasItemIndex);
      if (alreadyHas && !hasMore) {
        listElements.remove(lasItemIndex);
        fireIntervalRemoved(this, lasItemIndex, lasItemIndex);
      }

      if (!alreadyHas && hasMore) {
        listElements.add(new SearchEverywhereFoundElementInfo(MORE_ELEMENT, 0, null));
        lasItemIndex += 1;
        fireIntervalAdded(this, lasItemIndex, lasItemIndex);
      }
    }

    public void clear() {
      int index = listElements.size() - 1;
      listElements.clear();
      hasMoreContributors.clear();
      if (index >= 0) {
        fireIntervalRemoved(this, 0, index);
      }
    }

    public boolean contains(Object val) {
      return values().contains(val);
    }

    public boolean isMoreElement(int index) {
      return listElements.get(index).getElement() == MORE_ELEMENT;
    }

    public <Item> SearchEverywhereContributor<Item> getContributorForIndex(int index) {
      //noinspection unchecked
      return (SearchEverywhereContributor<Item>)listElements.get(index).getContributor();
    }

    public Map<SearchEverywhereContributor<?>, Collection<SearchEverywhereFoundElementInfo>> getFoundElementsMap() {
      return listElements.stream()
        .filter(info -> info.element != MORE_ELEMENT)
        .collect(Collectors.groupingBy(o -> o.getContributor(), Collectors.toCollection(ArrayList::new)));
    }

    @NotNull
    private List<Object> values() {
      return Lists.transform(listElements, info -> info.getElement());
    }
  }

  private class ShowInFindToolWindowAction extends DumbAwareAction {

    ShowInFindToolWindowAction() {
      super(IdeBundle.messagePointer("show.in.find.window.button.name"),
            IdeBundle.messagePointer("show.in.find.window.button.description"), AllIcons.General.Pin_tab);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      stopSearching();

      Collection<SearchEverywhereContributor<?>> contributors = getContributorsForCurrentTab();
      contributors = ContainerUtil.filter(contributors, SearchEverywhereContributor::showInFindResults);

      if (contributors.isEmpty()) {
        return;
      }

      String searchText = getSearchPattern();
      String contributorsString = contributors.stream()
        .map(SearchEverywhereContributor::getGroupName)
        .collect(Collectors.joining(", "));

      UsageViewPresentation presentation = new UsageViewPresentation();
      String tabCaptionText = IdeBundle.message("searcheverywhere.found.matches.title", searchText, contributorsString);
      presentation.setCodeUsagesString(tabCaptionText);
      presentation.setUsagesInGeneratedCodeString(
        IdeBundle.message("searcheverywhere.found.matches.generated.code.title", searchText, contributorsString));
      presentation.setTargetsNodeText(IdeBundle.message("searcheverywhere.found.targets.title", searchText, contributorsString));
      presentation.setTabName(tabCaptionText);
      presentation.setTabText(tabCaptionText);

      Collection<Usage> usages = new LinkedHashSet<>();
      Collection<PsiElement> targets = new LinkedHashSet<>();

      Collection<Object> cached = contributors.stream()
        .flatMap(contributor -> myListModel.getFoundItems(contributor).stream())
        .collect(Collectors.toSet());
      fillUsages(cached, usages, targets);

      Collection<SearchEverywhereContributor<?>> contributorsForAdditionalSearch;
      contributorsForAdditionalSearch = ContainerUtil.filter(contributors, contributor -> myListModel.hasMoreElements(contributor));

      if (!contributorsForAdditionalSearch.isEmpty()) {
        ProgressManager.getInstance().run(new Task.Modal(myProject, tabCaptionText, true) {
          private final ProgressIndicator progressIndicator = new ProgressIndicatorBase();

          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            progressIndicator.start();
            TooManyUsagesStatus tooManyUsagesStatus = TooManyUsagesStatus.createFor(progressIndicator);

            Collection<Object> foundElements = new ArrayList<>();
            int alreadyFoundCount = cached.size();
            for (SearchEverywhereContributor<?> contributor : contributorsForAdditionalSearch) {
              if (progressIndicator.isCanceled()) break;
              try {
                fetch(contributor, foundElements, alreadyFoundCount, tooManyUsagesStatus);
              }
              catch (ProcessCanceledException ignore) {
              }
            }
            fillUsages(foundElements, usages, targets);
          }

          <Item> void fetch(SearchEverywhereContributor<Item> contributor,
                            Collection<Object> foundElements,
                            int alreadyFoundCount,
                            TooManyUsagesStatus tooManyUsagesStatus) {
            contributor.fetchElements(searchText, progressIndicator, o -> {
              if (progressIndicator.isCanceled()) {
                return false;
              }

              if (cached.contains(o)) {
                return true;
              }

              foundElements.add(o);
              tooManyUsagesStatus.pauseProcessingIfTooManyUsages();
              if (foundElements.size() + alreadyFoundCount >= UsageLimitUtil.USAGES_LIMIT &&
                  tooManyUsagesStatus.switchTooManyUsagesStatus()) {
                UsageViewManagerImpl.showTooManyUsagesWarningLater(getProject(), tooManyUsagesStatus, progressIndicator, null);
                return !progressIndicator.isCanceled();
              }
              return true;
            });
          }

          @Override
          public void onCancel() {
            progressIndicator.cancel();
          }

          @Override
          public void onSuccess() {
            showInFindWindow(targets, usages, presentation);
          }

          @Override
          public void onThrowable(@NotNull Throwable error) {
            super.onThrowable(error);
            progressIndicator.cancel();
          }
        });
      }
      else {
        showInFindWindow(targets, usages, presentation);
      }
      closePopup();
    }

    private void fillUsages(Collection<Object> foundElements, Collection<? super Usage> usages, Collection<? super PsiElement> targets) {
      ReadAction.run(() -> foundElements.stream()
        .filter(o -> o instanceof PsiElement)
        .forEach(o -> {
          PsiElement element = (PsiElement)o;
          if (element.getTextRange() != null) {
            UsageInfo usageInfo = new UsageInfo(element);
            usages.add(new UsageInfo2UsageAdapter(usageInfo));
          }
          else {
            targets.add(element);
          }
        }));
    }

    private void showInFindWindow(Collection<? extends PsiElement> targets, Collection<Usage> usages, UsageViewPresentation presentation) {
      UsageTarget[] targetsArray = targets.isEmpty() ? UsageTarget.EMPTY_ARRAY
                                                     : PsiElement2UsageTargetAdapter.convert(PsiUtilCore.toPsiElementArray(targets));
      Usage[] usagesArray = usages.toArray(Usage.EMPTY_ARRAY);
      UsageViewManager.getInstance(myProject).showUsages(targetsArray, usagesArray, presentation);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
      if (myProject == null) {
        e.getPresentation().setEnabled(false);
        return;
      }

      SearchEverywhereContributor<?> contributor = mySelectedTab == null ? null : mySelectedTab.contributor;
      e.getPresentation().setEnabled(contributor == null || contributor.showInFindResults());
      e.getPresentation().setIcon(ToolWindowManager.getInstance(myProject).getLocationIcon(ToolWindowId.FIND, AllIcons.General.Pin_tab));
    }
  }

  private class CompleteCommandAction extends DumbAwareAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      if (completeCommand()) {
        FeatureUsageData data = SearchEverywhereUsageTriggerCollector
          .createData(null)
          .addInputEvent(e);
        featureTriggered(SearchEverywhereUsageTriggerCollector.COMMAND_COMPLETED, data);
      }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
      e.getPresentation().setEnabled(getCompleteCommand().isPresent());
    }

    private boolean completeCommand() {
      Optional<SearchEverywhereCommandInfo> suggestedCommand = getCompleteCommand();
      if (suggestedCommand.isPresent()) {
        mySearchField.setText(suggestedCommand.get().getCommandWithPrefix() + " ");
        return true;
      }

      return false;
    }

    private Optional<SearchEverywhereCommandInfo> getCompleteCommand() {
      String pattern = getSearchPattern();
      String commandPrefix = SearchTopHitProvider.getTopHitAccelerator();
      if (pattern.startsWith(commandPrefix) && !pattern.contains(" ")) {
        String typedCommand = pattern.substring(commandPrefix.length());
        SearchEverywhereCommandInfo command = getSelectedCommand(typedCommand).orElseGet(() -> {
          List<SearchEverywhereCommandInfo> completions = getCommandsForCompletion(getContributorsForCurrentTab(), typedCommand);
          return completions.isEmpty() ? null : completions.get(0);
        });

        return Optional.ofNullable(command);
      }

      return Optional.empty();
    }
  }

  private String getNotFoundText() {
    return mySelectedTab.getContributor()
      .map(c -> IdeBundle.message("searcheverywhere.nothing.found.for.contributor.anywhere",
                                  c.getFullGroupName().toLowerCase(Locale.ROOT)))
      .orElse(IdeBundle.message("searcheverywhere.nothing.found.for.all.anywhere"));
  }

  private void featureTriggered(@NotNull String featureID, @Nullable FeatureUsageData data) {
    if (data != null) {
      SearchEverywhereUsageTriggerCollector.trigger(myProject, featureID, data);
    }
    else {
      SearchEverywhereUsageTriggerCollector.trigger(myProject, featureID);
    }
  }

  private final SearchListener mySearchListener = new SearchListener();

  private class SearchListener implements SESearcher.Listener {
    private Consumer<List<Object>> testCallback;

    @Override
    public void elementsAdded(@NotNull List<? extends SearchEverywhereFoundElementInfo> list) {
      boolean wasEmpty = myListModel.listElements.isEmpty();

      mySelectionTracker.lock();
      myListModel.addElements(list);
      mySelectionTracker.unlock();

      mySelectionTracker.restoreSelection();

      if (wasEmpty && !myListModel.listElements.isEmpty()) {
        Object prevSelection = ((SearchEverywhereManagerImpl)SearchEverywhereManager.getInstance(myProject))
          .getPrevSelection(getSelectedContributorID());
        if (prevSelection instanceof Integer) {
          for (SearchEverywhereFoundElementInfo info : myListModel.listElements) {
            if (Objects.hashCode(info.element) == ((Integer)prevSelection).intValue()) {
              myResultsList.setSelectedValue(info.element, true);
              break;
            }
          }
        }
      }
    }

    @Override
    public void elementsRemoved(@NotNull List<? extends SearchEverywhereFoundElementInfo> list) {
      list.forEach(info -> myListModel.removeElement(info.getElement(), info.getContributor()));
    }

    @Override
    public void searchFinished(@NotNull Map<SearchEverywhereContributor<?>, Boolean> hasMoreContributors) {
      if (myResultsList.isEmpty() || myListModel.isResultsExpired()) {
        if (myEverywhereAutoSet && !isEverywhere() && canToggleEverywhere() && !getSearchPattern().isEmpty()) {
          setEverywhereAuto(true);
          myNotFoundString = getSearchPattern();
          return;
        }

        hideHint();
        if (myListModel.isResultsExpired()) {
          myListModel.clear();
        }
      }

      myResultsList.setEmptyText(getSearchPattern().isEmpty() ? "" : getNotFoundText());
      hasMoreContributors.forEach(myListModel::setHasMore);

      myListModel.setMaxFrozenIndex(-1);
      mySelectionTracker.resetSelectionIfNeeded();

      if (testCallback != null) testCallback.consume(myListModel.getItems());
    }

    @TestOnly
    void setTestCallback(@Nullable Consumer<List<Object>> callback) {
      testCallback = callback;
    }
  }

  private final SearchEverywhereContributor<Object> myStubCommandContributor = new SearchEverywhereContributor<Object>() {
    @NotNull
    @Override
    public String getSearchProviderId() {
      return "CommandsContributor";
    }

    @NotNull
    @Override
    public String getGroupName() {
      return IdeBundle.message("searcheverywhere.commands.tab.name");
    }

    @Override
    public int getSortWeight() {
      return 10;
    }

    @Override
    public boolean showInFindResults() {
      return false;
    }

    @Override
    public void fetchElements(@NotNull String pattern,
                              @NotNull ProgressIndicator progressIndicator,
                              @NotNull Processor<? super Object> consumer) {}

    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
      mySearchField.setText(((SearchEverywhereCommandInfo)selected).getCommandWithPrefix() + " ");
      featureTriggered(SearchEverywhereUsageTriggerCollector.COMMAND_COMPLETED, null);
      return false;
    }

    @NotNull
    @Override
    public ListCellRenderer<? super Object> getElementsRenderer() {
      return myCommandRenderer;
    }

    @Nullable
    @Override
    public Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
      return null;
    }
  };
}
