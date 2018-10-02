package io.github.vladimirmi.internetradioplayer.data.manager

import android.util.Xml
import io.github.vladimirmi.internetradioplayer.data.db.entity.Group
import io.github.vladimirmi.internetradioplayer.data.db.entity.Station
import org.xmlpull.v1.XmlSerializer
import java.io.StringWriter

/**
 * Created by Vladimir Mikhalev 02.10.2018.
 */

class BackupRestoreHelper {

    fun createXml(groups: List<Group>): String {
        val serializer = Xml.newSerializer()
        val writer = StringWriter()
        serializer.setOutput(writer)

        serializer.startDocument("UTF-8", true)
        serializer.startTag("", "data")
        serializer.attribute("", "version", "1")

        writeStations(serializer, groups)
        writeGroups(serializer, groups)

        return writer.toString()
    }

    private fun writeStations(serializer: XmlSerializer, groups: List<Group>) {
        fun writeStation(station: Station, group: String) {
            serializer.startTag("", "station")
            with(station) {
                serializer.attribute("", "name", name)
                serializer.attribute("", "group", group)
                serializer.attribute("", "streamUri", uri)
                serializer.attribute("", "url", url)
                serializer.attribute("", "bitrate", bitrate.toString())
                serializer.attribute("", "sample", sample.toString())
                serializer.attribute("", "genre", genres.joinToString())
                serializer.attribute("", "order", order.toString())
            }
            serializer.endTag("", "station")
        }

        serializer.startTag("", "stations")
        groups.forEach { group -> group.stations.forEach { writeStation(it, group.name) } }
        serializer.endTag("", "stations")
    }

    private fun writeGroups(serializer: XmlSerializer, groups: List<Group>) {
        serializer.startTag("", "groups")
        groups.forEach { group ->
            serializer.startTag("", "group")
            with(group) {
                serializer.attribute("", "name", name)
                serializer.attribute("", "order", order.toString())
                serializer.attribute("", "expanded", expanded.toString())
            }
            serializer.endTag("", "group")
        }
        serializer.endTag("", "groups")
    }
}
