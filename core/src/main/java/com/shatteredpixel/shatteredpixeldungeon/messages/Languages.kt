/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2018 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.messages

import java.util.Locale

enum class Languages private constructor(private val name: String, private val code: String, private val status: Status, private val reviewers: Array<String>?, private val translators: Array<String>?) {
    ENGLISH("english", "", Status.REVIEWED, null, null),

    CHINESE("中文", "zh", Status.REVIEWED, arrayOf<String>("Jinkeloid(zdx00793)"), arrayOf<String>("931451545", "HoofBumpBlurryface", "Lery", "Lyn-0401", "ShatteredFlameBlast", "hmdzl001", "tempest102")),
    CZECH("čeština", "cs", Status.REVIEWED, arrayOf<String>("ObisMike"), arrayOf<String>("AshenShugar", "Buba237", "JStrange", "RealBrofessor", "chuckjirka")),
    CATALAN("català", "ca", Status.REVIEWED, arrayOf<String>("Illyatwo2"), null),

    KOREAN("한국어", "ko", Status.UNREVIEWED, arrayOf<String>("Flameblast12"), arrayOf<String>("Korean2017", "WondarRabb1t", "ddojin0115", "eeeei", "hancyel", "linterpreteur", "lsiebnie")),
    SPANISH("español", "es", Status.UNREVIEWED, arrayOf<String>("Kiroto", "Kohru", "grayscales"), arrayOf<String>("Alesxanderk", "CorvosUtopy", "Dewstend", "Dyrran", "Fervoreking", "Illyatwo2", "JPCHZ", "airman12", "alfongad", "benzarr410", "ctrijueque", "dhg121", "javifs", "jonismack1")),
    POLISH("polski", "pl", Status.UNREVIEWED, arrayOf<String>("Deksippos", "kuadziw"), arrayOf<String>("Chasseur", "Darden", "MJedi", "MrKukurykpl", "Peperos", "Scharnvirk", "Shmilly", "dusakus", "michaub", "ozziezombie", "szczoteczka22", "szymex73")),
    ITALIAN("italiano", "it", Status.UNREVIEWED, arrayOf<String>("bizzolino", "funnydwarf"), arrayOf<String>("4est", "DaniMare", "Danzl", "andrearubbino00", "nessunluogo", "righi.a", "umby000")),
    ESPERANTO("esperanto", "eo", Status.UNREVIEWED, arrayOf<String>("Verdulo"), arrayOf<String>("Raizin")),

    RUSSIAN("русский", "ru", Status.INCOMPLETE, arrayOf<String>("ConsideredHamster", "Inevielle", "yarikonline"), arrayOf<String>("AttHawk46", "HerrGotlieb", "HoloTheWise", "MrXantar", "Shamahan", "roman.yagodin", "un_logic", " Вoвa")),
    GERMAN("deutsch", "de", Status.INCOMPLETE, arrayOf<String>("Dallukas", "KrystalCroft", "Wuzzy", "Zap0", "davedude"), arrayOf<String>("DarkPixel", "ErichME", "LenzB", "Sarius", "Sorpl3x", "ThunfischGott", "Topicranger", "oragothen")),
    FRENCH("français", "fr", Status.INCOMPLETE, arrayOf<String>("Emether", "canc42", "kultissim", "minikrob"), arrayOf<String>("Alsydis", "Basttee", "Dekadisk", "Draal", "Neopolitan", "SpeagleZNT", "antoine9298", "go11um", "linterpreteur", "solthaar", "vavavoum")),
    PORTUGUESE("português", "pt", Status.INCOMPLETE, arrayOf<String>("TDF2001", "matheus208"), arrayOf<String>("ChainedFreaK", "JST", "MadHorus", "Tio_P_", "ancientorange", "danypr23", "ismael.henriques12", "owenreilly", "try31")),
    FINNISH("suomi", "fi", Status.INCOMPLETE, arrayOf<String>("TenguTheKnight"), null),
    TURKISH("türkçe", "tr", Status.INCOMPLETE, arrayOf<String>("LokiofMillenium", "emrebnk"), arrayOf<String>("AcuriousPotato", "alpekin98", "denizakalin", "melezorus34")),
    HUNGARIAN("magyar", "hu", Status.INCOMPLETE, arrayOf<String>("dorheim"), arrayOf<String>("Navetelen", "clarovani", "dhialub", "nanometer", "nardomaa")),
    INDONESIAN("indonésien", "in", Status.INCOMPLETE, arrayOf<String>("rakapratama"), null);

    enum class Status {
        //below 80% complete languages are not added.
        INCOMPLETE, //80-99% complete
        UNREVIEWED, //100% complete
        REVIEWED    //100% reviewed
    }

    fun nativeName(): String {
        return name
    }

    fun code(): String {
        return code
    }

    fun status(): Status {
        return status
    }

    fun reviewers(): Array<String> {
        return reviewers?.clone() ?: arrayOf()
    }

    fun translators(): Array<String> {
        return translators?.clone() ?: arrayOf()
    }

    companion object {

        fun matchLocale(locale: Locale): Languages {
            return matchCode(locale.language)
        }

        fun matchCode(code: String): Languages {
            for (lang in Languages.values()) {
                if (lang.code() == code)
                    return lang
            }
            return ENGLISH
        }
    }

}
