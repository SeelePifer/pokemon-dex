package com.pokemondex;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.pokemondex", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsIndependent = classes()
            .that().resideInAPackage("com.pokemondex.domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "com.pokemondex.domain..",
                    "java..",
                    "javax.."
            );

    @ArchTest
    static final ArchRule applicationIsIndependent = classes()
            .that().resideInAPackage("com.pokemondex.application..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "com.pokemondex.application..",
                    "com.pokemondex.domain..",
                    "java..",
                    "javax.."
            );

    @ArchTest
    static final ArchRule webDoesNotDependOnInfrastructure = noClasses()
            .that().resideInAPackage("com.pokemondex.web..")
            .should().dependOnClassesThat().resideInAPackage("com.pokemondex.infrastructure..");
}
