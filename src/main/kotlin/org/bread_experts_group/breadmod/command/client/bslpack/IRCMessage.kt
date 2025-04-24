package org.bread_experts_group.breadmod.command.client.bslpack

import java.io.InputStream
import java.io.OutputStream

class IRCMessage(
	val tags: Map<String, String>,
	val source: String,
	val command: String,
	val parameters: String
) {
	override fun toString(): String = "(IRC, $command) " +
			(if (tags.isNotEmpty()) "TAGS [${tags.size}][${tags.entries.joinToString(";") { "${it.key}=${it.value}" }}] "
			else "") +
			(if (source.isNotEmpty()) "SOURCE [$source] " else "") +
			"<$parameters>"

	fun write(stream: OutputStream) {
		if (tags.isNotEmpty())
			stream.writeString("@${tags.entries.joinToString(";") { "${it.key}=${it.value}" }} ")
		if (source.isNotEmpty())
			stream.writeString(":$source ")
		stream.writeString("$command${if (parameters.isNotEmpty()) " $parameters" else ""}\r\n")
	}

	companion object {
		fun read(stream: InputStream): IRCMessage {
			var initialA = Char(stream.read())
			val tags = if (initialA == '@') buildMap {
				val readTags = stream.scanDelimiter(" ")
				readTags.split(';').forEach {
					val (key, value) = it.split('=')
					this[key] = value
				}
				initialA = Char(stream.read())
			} else emptyMap()
			val source = if (initialA == ':') {
				val read = stream.scanDelimiter(" ")
				initialA = Char(stream.read())
				read
			} else ""
			val (command, arguments) = stream.scanDelimiter("\r\n").split(' ', limit = 2)
			return IRCMessage(tags, source, initialA + command, arguments)
		}
	}
}