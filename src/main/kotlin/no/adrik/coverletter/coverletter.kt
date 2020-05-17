package no.adrik.coverletter

/**
 * Author: Samir Adrik
 * Email:  samir.adrik@gmail.com
 */

import java.io.File
import java.io.InputStream
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CoverLetter(
    private var companyName: String,
    private var companyAddress: String,
    private var companyZipCodeInfo: String,
    private var position: String,
    private var field: String = "statistikk og analyse",
    private var searchEngine: String = "finn.no",
    private var suckUpLine: String = "ett av Norges ledende selskap"
) {

    private val resourcesPath: String
    private val preamble: String
    private val education: String
    private val experience: String


    init {
        val alpha = "^[a-zæøåA-ZÆØÅ\\s]*".toRegex()
        val alphaDot = "^[.a-zæøåA-ZÆØÅ\\s]*".toRegex()
        val alphaNumeric = "^[a-zæøåA-ZÆØÅ0-9\\s]*$".toRegex()
        val alphaNumericBackSlashDash = "^[/\\-a-zæøåA-ZÆØÅ0-9\\s]*$".toRegex()

        require(companyName.matches(alpha)) {
            "companyName can only contain alphabetic characters, got '$companyName'"
        }

        require(companyAddress.matches(alphaNumeric)) {
            "companyAdress can only contain alphanumerical characters, got '$companyAddress'"
        }

        require(companyZipCodeInfo.matches(alphaNumeric)) {
            "companyZipCodeInfo can only contain alphanumerical characters, got '$companyZipCodeInfo'"
        }

        require(position.matches(alphaNumericBackSlashDash)) {
            "position can only contain alphabetic characters, got '$position'"
        }

        require(field.matches(alpha)) {
            "positionField can only contain alphabetic characters, got '$field'"
        }

        require(searchEngine.matches(alphaDot)) {
            "searchEngine can only contain alphabetic characters, got '$searchEngine'"
        }

        require(suckUpLine.matches(alpha)) {
            "suckUpLine can only contain alphabetic characters, got '$suckUpLine'"
        }
        this.resourcesPath = "${Paths.get("").toAbsolutePath()}/src/main/resources/"
        this.preamble = "${readFromResources("preamble.txt")}\n\n"
        this.education = "${readFromResources("education.txt")}\n\n"
        this.experience = "${readFromResources("experience.txt")}\n\n"
    }

    private fun readFromResources(fileName: String): String {
        val inputStream: InputStream = File("${resourcesPath}$fileName").inputStream()
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun createHeader(): String {
        val baseHeader = readFromResources("header.txt")
        return "${baseHeader.replace("companyName", companyName).replace("companyAddress", companyAddress)
            .replace("companyZipCodeInfo", companyZipCodeInfo)}\n\n"
    }

    private fun createDateStamp(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MMMM.yyyy")
        val today = now.format(formatter)
        val baseDate = readFromResources("date_stamp.txt")
        return "${baseDate.replace("today", today)}\n\n"
    }

    private fun createTitle(): String {
        val baseTitle = readFromResources("title.txt")
        return "${baseTitle.replace("position", position).replace("companyName", companyName)}\n\n"
    }

    private fun createIntro(): String {
        val baseIntro = readFromResources("intro.txt")
        return "${baseIntro.replace("companyName", companyName).replace("position", position)
            .replace("field", field).replace("searchEngine", searchEngine)
            .replace("suckUpLine", suckUpLine)}\n\n"
    }

    private fun createEnding(): String {
        val baseEnding = readFromResources("ending.txt")
        return "${baseEnding.replace("position", position).replace("companyName", companyName)}\n\n"
    }

    fun createCoverLetter() {
        println(
            "$preamble${createHeader()}${createDateStamp()}${createTitle()}" +
                    "${createIntro()}$education$experience${createEnding()}"
        )
    }
}

fun main() {
    val coverLetter = CoverLetter(
        "Retriever", "Langkaia 1", "0150 Oslo",
        "Dataanalytiker / Bi-analytiker", suckUpLine = "Nordens største private medieanalysemiljø"
    )
    coverLetter.createCoverLetter()
}