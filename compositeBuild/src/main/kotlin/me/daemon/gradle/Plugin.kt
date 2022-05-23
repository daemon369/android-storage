package me.daemon.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class Plugin : Plugin<Project> {
    override fun apply(target: Project) = Unit
}