package com.example.dever.nonUglyDataUsageWidget

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import org.hamcrest.CoreMatchers.*
import org.hamcrest.text.IsEqualIgnoringCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for PrePermissionRequestActivity
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class PrePermissionRequestActivityTest {

    var device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    init {
        // reset "don't ask again" state, before activity is created
//        device.executeShellCommand("pm clear com.example.dever.nonUglyDataUsageWidget")
    }

    @get:Rule
    var rule = ActivityTestRule(PrePermissionRequestActivity::class.java, true, false)
    val p2btn = onView(withId(R.id.prepermission_phone_button))
    val p2msg = onView(withId(R.id.prepermission_granted_text_p2))
    val right = onView(withId(R.id.imgPrePermRightArrow))
    val p3btn = onView(withId(R.id.usage_grant_permission_button))
    val p3msg = onView(withId(R.id.prepermission_granted_text_p3))

    private val waitTimeout = 5000L


    @Before
    fun setup() {
        // revoke permissions - test state is self contained but emulator isn't
        device.executeShellCommand("pm revoke com.example.dever.nonUglyDataUsageWidget android.permission.READ_PHONE_STATE")

        // launch activity after the 2 pm commands otherwise it dies
        rule.launchActivity(Intent(InstrumentationRegistry.getTargetContext(), PrePermissionRequestActivity::class.java))

        // can't revoke special permissions with pm
        device.executeShellCommand("am start -a android.settings.USAGE_ACCESS_SETTINGS")
        device.wait(Until.hasObject(By.textContains("usage access")), waitTimeout)
        device.findObject(By.text("NUD")).click()
        device.wait(Until.hasObject(By.text("Permit usage access")), waitTimeout)
        val switch = device.findObject(By.res("android:id/switch_widget"))
        if (switch.isChecked) switch.click()
        device.pressBack()
        device.pressBack()

        onView(withId(R.id.imgPrePermRightArrow)).perform(click())
    }

    @After
    fun teardown() {
        // hit home to dismiss any runtime permission dialogs that are hanging around
        device.pressHome()
    }


    @Test
    fun firstRunUserGrantsAllPermissions() {
        // button visible until clicked
        // field not visible until button clicked
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed()))
                .perform(click())

        // click allow button on system "grant perm?" dialog present, wait for focus to return
        device.findObject(UiSelector().text("ALLOW")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        p2btn.check(matches(not(isDisplayed())))
        p2msg.check(matches(isDisplayed()))

        // next page
        right.perform(click())

        p3msg.check(matches(not(isDisplayed())))
        p3btn.check(matches(isDisplayed())).perform(click())

        // system usage access activity
        device.wait(Until.hasObject(By.text("apps with usage access")), waitTimeout)
        device.findObject(By.text("NUD")).click()
        device.wait(Until.hasObject(By.text("Permit usage access")), waitTimeout)
        device.findObject(By.res("android:id/switch_widget")).click()
        device.pressBack()
        device.pressBack()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        p3msg.check(matches(isDisplayed()))
        p3btn.check(matches(not(isDisplayed())))

        right.perform(click())
    }

    @Test
    fun firstRunUserGrantsPhoneThenCancelsUsingBack() {
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        // click allow button on system "grant perm?" dialog present, wait for focus to return
        device.findObject(UiSelector().text("DENY")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        device.pressBack()

        onView(withResourceName("button1"))  // r u sure dialog continue button
                .check(matches(isDisplayed()))
                .perform(click())

    }

    @Test
    fun firstRunUserGrantsPhoneThenCancelsUsingRightArrow() {
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        // click allow button on system "grant perm?" dialog present, wait for focus to return
        device.findObject(UiSelector().text("DENY")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        right.perform(click())

        onView(withResourceName("button1"))  // r u sure dialog continue button
                .check(matches(isDisplayed()))
                .perform(click())

    }


    @Test
    fun firstRunUserGrantsPhoneThenCancelsThenResumes() {
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        // click allow button on system "grant perm?" dialog present, wait for focus to return
        device.findObject(UiSelector().text("DENY")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        right.perform(click())

        onView(withResourceName("button2"))  // r u sure dialog continue button
                .check(matches(isDisplayed()))
                .perform(click())

        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        device.findObject(UiSelector().text("ALLOW")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)

        p2msg.check(matches(isDisplayed()))
        p2btn.check(matches(not(isDisplayed())))

    }

    @Test
    fun firstRunUserCancelsUsingBack() {
        p2msg.check(matches(not(isDisplayed())))

        device.pressBack()

        onView(allOf(
                instanceOf(android.widget.TextView::class.java),
                withText("Continue without granting permission?")
        )).check(matches(isDisplayed()))

        onView(allOf(
                instanceOf(android.widget.Button::class.java),
                withText(IsEqualIgnoringCase("Continue"))
        )).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun firstRunUserDeniesNeverAskAgainPhone() {
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        // 1st deny
        device.findObject(UiSelector().text("DENY")).click()
        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)
        p2btn.check(matches(isDisplayed())).perform(click())
        // 2nd deny, with "don't ask again"
        device.findObject(UiSelector().resourceId("com.android.packageinstaller:id/do_not_ask_checkbox")).click()
        device.findObject(UiSelector().text("DENY")).click()

        device.wait(Until.hasObject(By.text("NUD")), waitTimeout)
        p2msg.check(matches(not(isDisplayed())))
        p2btn.check(matches(isDisplayed())).perform(click())

        onView(withText(containsString("You've denied"))).check(matches(isDisplayed()))
        onView(allOf(
                withText(IsEqualIgnoringCase("Grant permission")),
                instanceOf(android.widget.Button::class.java)
        )).check(matches(isDisplayed())).perform(click())

        device.wait(Until.hasObject(By.text("App info")), waitTimeout)
    }
}