package com.example.bloggingapp.modals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(
    tableName="auth_token",
foreignKeys= [
    //Setting the foreign key with the table(parent)
    ForeignKey(
        entity=AccountProperties::class,
        parentColumns=["pk"],
        childColumns=["account_pk"],
        onDelete=CASCADE //If a row in the table Account properties got deleted then all of its forign keys must be deleted so that he will no longer exist
    )
]
)
data class AuthToken(
    //We will be having a foreign key relation pointing to the account Properties table
//Which means primary key of the Auth Token table is going to refer to the primary key of Auth propertes
@PrimaryKey
@ColumnInfo(
    name="account_pk"

)
//We are going to take this feild from the Account Propeties database
var account_pk:Int?=-1, //If foreign key relation is not setup then we will understand that relation has not been setup

@SerializedName("token")
@Expose                         //WE are retriving the token and based on that we will give the user permision to handle posts
@ColumnInfo(name="token")
var token:String?=null  //Token can be null because user can be non-autheticated



)