package com.safenest.app.model

import com.google.gson.annotations.SerializedName


data class FullAddress (
    @SerializedName("items" ) var items : ArrayList<Items> = arrayListOf()
)

data class Items (
    @SerializedName("title"           ) var title           : String?           = null,
    @SerializedName("id"              ) var id              : String?           = null,
    @SerializedName("resultType"      ) var resultType      : String?           = null,
    @SerializedName("houseNumberType" ) var houseNumberType : String?           = null,
    @SerializedName("address"         ) var address         : Address?          = Address(),
    @SerializedName("position"        ) var position        : Position?         = Position(),
    @SerializedName("access"          ) var access          : ArrayList<Access> = arrayListOf(),
    @SerializedName("distance"        ) var distance        : Int?              = null,
    @SerializedName("mapView"         ) var mapView         : MapView?          = MapView()
)

data class Address (
    @SerializedName("label"       ) var label       : String? = null,
    @SerializedName("countryCode" ) var countryCode : String? = null,
    @SerializedName("countryName" ) var countryName : String? = null,
    @SerializedName("stateCode"   ) var stateCode   : String? = null,
    @SerializedName("state"       ) var state       : String? = null,
    @SerializedName("county"      ) var county      : String? = null,
    @SerializedName("city"        ) var city        : String? = null,
    @SerializedName("district"    ) var district    : String? = null,
    @SerializedName("subdistrict" ) var subdistrict : String? = null,
    @SerializedName("street"      ) var street      : String? = null,
    @SerializedName("postalCode"  ) var postalCode  : String? = null,
    @SerializedName("houseNumber" ) var houseNumber : String? = null
)

data class Position (
    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null
)

data class Access (
    @SerializedName("lat" ) var lat : Double? = null,
    @SerializedName("lng" ) var lng : Double? = null
)

data class MapView (
    @SerializedName("west"  ) var west  : Double? = null,
    @SerializedName("south" ) var south : Double? = null,
    @SerializedName("east"  ) var east  : Double? = null,
    @SerializedName("north" ) var north : Double? = null
)