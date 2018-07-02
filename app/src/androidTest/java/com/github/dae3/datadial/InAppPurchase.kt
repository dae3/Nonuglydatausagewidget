package com.github.dae3.datadial

import android.app.Activity
import com.android.billingclient.api.*

private const val skuPremium = "PREMIUM"

class InAppPurchase(private val activity: Activity) : PurchasesUpdatedListener, BillingClientStateListener {

    private var _hasPurchasedPremium = false
    private val billingClient = BillingClient.newBuilder(activity).setListener(this).build()

    val hasPurchasedPremium
        get() = _hasPurchasedPremium

    init {
        billingClient.startConnection(this)
    }

    fun purchasePremium() {
        val success = billingClient.launchBillingFlow(
                this.activity,
                BillingFlowParams.newBuilder()
                        .setSku(skuPremium)
                        .setType(BillingClient.SkuType.INAPP)
                        .build()
        )
    }

    private fun queryPurchasedPremium() {
        _hasPurchasedPremium = billingClient
                .queryPurchases(BillingClient.SkuType.INAPP)
                .purchasesList.any { purchase -> purchase.sku == skuPremium  }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        // purchase completed (including started from outside)
        queryPurchasedPremium()
    }

    override fun onBillingServiceDisconnected() {
        // retry connect, implemented cacheing strategy
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBillingSetupFinished(responseCode: Int) {
        queryPurchasedPremium()
    }
}
