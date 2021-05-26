package fr.cornier.phonegg

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Summoner() : RealmObject() {
    var summonerId:String = ""
    var summonerAccountId:String  = ""
    var summonerPuuid:String  = ""
    var summonerRegion = ""
}