package com.safenest.app.util

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("keep_state_fragment")
class KeepStateNavigator(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : Navigator<KeepStateNavigator.Destination>() {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        val tag = destination.id.toString()
        var fragment = fragmentManager.findFragmentByTag(tag)

        if (fragment == null) {
            fragment = fragmentManager.fragmentFactory.instantiate(
                context.classLoader,
                destination.className
            )
            fragment.arguments = args
            fragmentManager.beginTransaction()
                .add(containerId, fragment, tag)
                .commitNow()
        }

        val transaction = fragmentManager.beginTransaction()

        fragmentManager.fragments.forEach { transaction.hide(it) }
        transaction.show(fragment).commitNow()

        return destination
    }

    override fun createDestination(): Destination = Destination(this)

    override fun popBackStack(): Boolean {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    @NavDestination.ClassType(Fragment::class)
    class Destination(navigator: Navigator<out Destination>) : NavDestination(navigator) {
        var className: String = ""
    }
}
