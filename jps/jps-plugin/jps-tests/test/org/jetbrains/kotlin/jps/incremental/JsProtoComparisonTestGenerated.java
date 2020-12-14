/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@RunWith(JUnit3RunnerWithInners.class)
public class JsProtoComparisonTestGenerated extends AbstractJsProtoComparisonTest {
    @TestMetadata("jps-plugin/testData/comparison/classSignatureChange")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ClassSignatureChange extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInClassSignatureChange() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("classAnnotationListChanged")
        public void testClassAnnotationListChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/classAnnotationListChanged/");
        }

        @TestMetadata("classFlagsAndMembersChanged")
        public void testClassFlagsAndMembersChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/classFlagsAndMembersChanged/");
        }

        @TestMetadata("classFlagsChanged")
        public void testClassFlagsChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/classFlagsChanged/");
        }

        @TestMetadata("classTypeParameterListChanged")
        public void testClassTypeParameterListChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/classTypeParameterListChanged/");
        }

        @TestMetadata("classWithSuperTypeListChanged")
        public void testClassWithSuperTypeListChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/classWithSuperTypeListChanged/");
        }

        @TestMetadata("nestedClassSignatureChanged")
        public void testNestedClassSignatureChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/nestedClassSignatureChanged/");
        }

        @TestMetadata("sealedClassImplAdded")
        public void testSealedClassImplAdded() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplAdded/");
        }

        @TestMetadata("sealedClassImplRemoved")
        public void testSealedClassImplRemoved() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplRemoved/");
        }

        @TestMetadata("sealedClassNestedImplAdded")
        public void testSealedClassNestedImplAdded() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAdded/");
        }

        @TestMetadata("sealedClassNestedImplAddedDeep")
        public void testSealedClassNestedImplAddedDeep() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAddedDeep/");
        }

        @TestMetadata("sealedClassNestedImplRemoved")
        public void testSealedClassNestedImplRemoved() throws Exception {
            runTest("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplRemoved/");
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/classAnnotationListChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassAnnotationListChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassAnnotationListChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/classAnnotationListChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/classFlagsAndMembersChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassFlagsAndMembersChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassFlagsAndMembersChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/classFlagsAndMembersChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/classFlagsChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassFlagsChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassFlagsChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/classFlagsChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/classTypeParameterListChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassTypeParameterListChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassTypeParameterListChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/classTypeParameterListChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/classWithSuperTypeListChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithSuperTypeListChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithSuperTypeListChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/classWithSuperTypeListChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/nestedClassSignatureChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class NestedClassSignatureChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInNestedClassSignatureChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/nestedClassSignatureChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplAdded")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassImplAdded extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassImplAdded() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplAdded"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplRemoved")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassImplRemoved extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassImplRemoved() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/sealedClassImplRemoved"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAdded")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassNestedImplAdded extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassNestedImplAdded() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAdded"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAddedDeep")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassNestedImplAddedDeep extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassNestedImplAddedDeep() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplAddedDeep"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplRemoved")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassNestedImplRemoved extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassNestedImplRemoved() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classSignatureChange/sealedClassNestedImplRemoved"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }

    @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ClassPrivateOnlyChange extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInClassPrivateOnlyChange() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("classWithPrivateFunChanged")
        public void testClassWithPrivateFunChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateFunChanged/");
        }

        @TestMetadata("classWithPrivatePrimaryConstructorChanged")
        public void testClassWithPrivatePrimaryConstructorChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivatePrimaryConstructorChanged/");
        }

        @TestMetadata("classWithPrivateSecondaryConstructorChanged")
        public void testClassWithPrivateSecondaryConstructorChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateSecondaryConstructorChanged/");
        }

        @TestMetadata("classWithPrivateValChanged")
        public void testClassWithPrivateValChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateValChanged/");
        }

        @TestMetadata("classWithPrivateVarChanged")
        public void testClassWithPrivateVarChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateVarChanged/");
        }

        @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateFunChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithPrivateFunChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithPrivateFunChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateFunChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivatePrimaryConstructorChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithPrivatePrimaryConstructorChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithPrivatePrimaryConstructorChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivatePrimaryConstructorChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateSecondaryConstructorChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithPrivateSecondaryConstructorChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithPrivateSecondaryConstructorChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateSecondaryConstructorChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateValChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithPrivateValChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithPrivateValChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateValChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateVarChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithPrivateVarChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithPrivateVarChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classPrivateOnlyChange/classWithPrivateVarChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }

    @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ClassMembersOnlyChanged extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInClassMembersOnlyChanged() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("classWithCompanionObjectChanged")
        public void testClassWithCompanionObjectChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithCompanionObjectChanged/");
        }

        @TestMetadata("classWithConstructorChanged")
        public void testClassWithConstructorChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithConstructorChanged/");
        }

        @TestMetadata("classWithFunAndValChanged")
        public void testClassWithFunAndValChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithFunAndValChanged/");
        }

        @TestMetadata("classWithNestedClassesChanged")
        public void testClassWithNestedClassesChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithNestedClassesChanged/");
        }

        @TestMetadata("classWitnEnumChanged")
        public void testClassWitnEnumChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/classWitnEnumChanged/");
        }

        @TestMetadata("defaultValues")
        public void testDefaultValues() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/defaultValues/");
        }

        @TestMetadata("membersAnnotationListChanged")
        public void testMembersAnnotationListChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/membersAnnotationListChanged/");
        }

        @TestMetadata("membersFlagsChanged")
        public void testMembersFlagsChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/membersFlagsChanged/");
        }

        @TestMetadata("nestedClassMembersChanged")
        public void testNestedClassMembersChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/nestedClassMembersChanged/");
        }

        @TestMetadata("sealedClassImplAdded")
        public void testSealedClassImplAdded() throws Exception {
            runTest("jps-plugin/testData/comparison/classMembersOnlyChanged/sealedClassImplAdded/");
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithCompanionObjectChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithCompanionObjectChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithCompanionObjectChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithCompanionObjectChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithConstructorChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithConstructorChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithConstructorChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithConstructorChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithFunAndValChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithFunAndValChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithFunAndValChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithFunAndValChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithNestedClassesChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWithNestedClassesChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWithNestedClassesChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/classWithNestedClassesChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/classWitnEnumChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class ClassWitnEnumChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInClassWitnEnumChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/classWitnEnumChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/defaultValues")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class DefaultValues extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInDefaultValues() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/defaultValues"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/membersAnnotationListChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class MembersAnnotationListChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInMembersAnnotationListChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/membersAnnotationListChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/membersFlagsChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class MembersFlagsChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInMembersFlagsChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/membersFlagsChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/nestedClassMembersChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class NestedClassMembersChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInNestedClassMembersChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/nestedClassMembersChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/classMembersOnlyChanged/sealedClassImplAdded")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class SealedClassImplAdded extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInSealedClassImplAdded() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/classMembersOnlyChanged/sealedClassImplAdded"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }

    @TestMetadata("jps-plugin/testData/comparison/packageMembers")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class PackageMembers extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInPackageMembers() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("defaultValues")
        public void testDefaultValues() throws Exception {
            runTest("jps-plugin/testData/comparison/packageMembers/defaultValues/");
        }

        @TestMetadata("membersAnnotationListChanged")
        public void testMembersAnnotationListChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/packageMembers/membersAnnotationListChanged/");
        }

        @TestMetadata("membersFlagsChanged")
        public void testMembersFlagsChanged() throws Exception {
            runTest("jps-plugin/testData/comparison/packageMembers/membersFlagsChanged/");
        }

        @TestMetadata("packageFacadePrivateOnlyChanges")
        public void testPackageFacadePrivateOnlyChanges() throws Exception {
            runTest("jps-plugin/testData/comparison/packageMembers/packageFacadePrivateOnlyChanges/");
        }

        @TestMetadata("packageFacadePublicChanges")
        public void testPackageFacadePublicChanges() throws Exception {
            runTest("jps-plugin/testData/comparison/packageMembers/packageFacadePublicChanges/");
        }

        @TestMetadata("jps-plugin/testData/comparison/packageMembers/defaultValues")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class DefaultValues extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInDefaultValues() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers/defaultValues"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/packageMembers/membersAnnotationListChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class MembersAnnotationListChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInMembersAnnotationListChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers/membersAnnotationListChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/packageMembers/membersFlagsChanged")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class MembersFlagsChanged extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInMembersFlagsChanged() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers/membersFlagsChanged"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/packageMembers/packageFacadePrivateOnlyChanges")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class PackageFacadePrivateOnlyChanges extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInPackageFacadePrivateOnlyChanges() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers/packageFacadePrivateOnlyChanges"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/packageMembers/packageFacadePublicChanges")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class PackageFacadePublicChanges extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInPackageFacadePublicChanges() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/packageMembers/packageFacadePublicChanges"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }

    @TestMetadata("jps-plugin/testData/comparison/unchanged")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Unchanged extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInUnchanged() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/unchanged"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("unchangedClass")
        public void testUnchangedClass() throws Exception {
            runTest("jps-plugin/testData/comparison/unchanged/unchangedClass/");
        }

        @TestMetadata("unchangedPackageFacade")
        public void testUnchangedPackageFacade() throws Exception {
            runTest("jps-plugin/testData/comparison/unchanged/unchangedPackageFacade/");
        }

        @TestMetadata("jps-plugin/testData/comparison/unchanged/unchangedClass")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class UnchangedClass extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInUnchangedClass() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/unchanged/unchangedClass"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }

        @TestMetadata("jps-plugin/testData/comparison/unchanged/unchangedPackageFacade")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class UnchangedPackageFacade extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInUnchangedPackageFacade() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/unchanged/unchangedPackageFacade"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }

    @TestMetadata("jps-plugin/testData/comparison/jsOnly")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class JsOnly extends AbstractJsProtoComparisonTest {
        private void runTest(String testDataFilePath) throws Exception {
            KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
        }

        public void testAllFilesPresentInJsOnly() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/jsOnly"), Pattern.compile("^([^\\.]+)$"), null, true);
        }

        @TestMetadata("externals")
        public void testExternals() throws Exception {
            runTest("jps-plugin/testData/comparison/jsOnly/externals/");
        }

        @TestMetadata("jps-plugin/testData/comparison/jsOnly/externals")
        @TestDataPath("$PROJECT_ROOT")
        @RunWith(JUnit3RunnerWithInners.class)
        public static class Externals extends AbstractJsProtoComparisonTest {
            private void runTest(String testDataFilePath) throws Exception {
                KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
            }

            public void testAllFilesPresentInExternals() throws Exception {
                KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/comparison/jsOnly/externals"), Pattern.compile("^([^\\.]+)$"), null, true);
            }
        }
    }
}
