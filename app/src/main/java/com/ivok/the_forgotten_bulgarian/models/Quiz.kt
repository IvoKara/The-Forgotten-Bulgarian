package com.ivok.the_forgotten_bulgarian.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Level(
    var name: String? = null,
    var number: Int? = null,
    var questions: List<Question>? = null
) : Parcelable

@Parcelize
class Question(
    var text: String? = null,
    var photoUrl: String? = null,
    var answer: String? = null,
    var level: Int? = null,
    var hint: String? = null,
    var additional: String? = null,
) : Parcelable