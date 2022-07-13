package com.sesolutions.responses.store.checkout

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.PaginationHelper

data class CheckoutResponse(val result: Result,
                            @SerializedName("session_id")
                            val sessionId: String) : ErrorResponse() {
    data class Result(
            val cartData: List<CartData>?,
            val extraParams: ExtraParams,
            @SerializedName("price_title")
            val priceTitle: String,
            val priceDetails: List<PriceDetails>,
            val checkout: String,
            val orders: List<Order>?,
            val checkouturl: String,
            @SerializedName("order_total")
            val orderTotal: String
    ) : PaginationHelper() {
        data class ExtraParams(
                @SerializedName("continue")
                val continueX: String,
                val empty: String,
                val update: String
        )

        data class CartData(
                val productData: List<ProductData>,
                @SerializedName("store_title")
                val storeTitle: String,
                @SerializedName("sub_total")
                val subTotal: String
        ) {
            data class ProductData(
                    val buttons: List<Button>,
                    val price: String,
                    @SerializedName("product_id")
                    val productId: Int,
                    val quantity: Int,
                    @SerializedName("cart_error")
                    val cartError: String,
                    @SerializedName("product_images")
                    val productImages: ProductImages,
                    val title: String
            ) {
                data class Button(
                        val id: Int,
                        val label: String,
                        val name: String
                )

                data class ProductImages(
                        val icon: String,
                        val main: String,
                        val normal: String,
                        val normalMain: String
                )
            }
        }

        data class PriceDetails(
                @SerializedName("title")
                val title: String,
                @SerializedName("price")
                val price: String
        )
    }
}