package lol.koblizek.bytelens.core.utils;

import dev.mccue.resolve.Artifact;
import dev.mccue.resolve.Group;
import dev.mccue.resolve.Version;
import dev.mccue.resolve.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public record MavenMetadata(
        Group group,
        Artifact artifact,
        Version latest,
        Version release,
        List<Version> versions,
        LocalDateTime lastUpdated
) {

    public static final Logger LOGGER = LoggerFactory.getLogger("MavenMetadata Fetcher");

    public MavenMetadata(
            Group group,
            Artifact artifact,
            Version latest,
            Version release,
            List<Version> versions,
            LocalDateTime lastUpdated
    ) {
        this.group = Objects.requireNonNull(group);
        this.artifact = Objects.requireNonNull(artifact);
        this.latest = Objects.requireNonNull(latest);
        this.release = Objects.requireNonNull(release);
        this.versions = List.copyOf(versions);
        this.lastUpdated = Objects.requireNonNull(lastUpdated);
    }

    public static MavenMetadata fromMavenCentral(String artifact) {
        String[] parts = artifact.split(":");
        return parseXml(StringUtils.readRemote("https://repo1.maven.org/maven2/" +
                String.join("/", parts[0].split("\\.")) + "/" + parts[1] + "/maven-metadata.xml"));
    }

    public static MavenMetadata from(String repository, String artifact) {
        String[] parts = artifact.split(":");
        return parseXml(StringUtils.readRemote(repository + "/" +
                String.join("/", parts[0].split("\\.")) + "/" + parts[1] + "/maven-metadata.xml"));
    }

    public static MavenMetadata parseXml(String content) {
        var handler = new DefaultHandler() {
            Group group;
            Artifact artifact;
            Version latest;
            Version release;
            final List<Version> versions = new ArrayList<>();
            LocalDateTime lastUpdated;

            final StringBuilder characterBuffer = new StringBuilder();
            Consumer<String> next = __ -> {};

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                switch (qName) {
                    case "groupId" -> next = groupId ->
                            this.group = new Group(groupId);
                    case "artifactId" -> next = artifactId ->
                            this.artifact = new Artifact(artifactId);
                    case "latest" -> next = lat ->
                            this.latest = new Version(lat);
                    case "release" -> next = rel ->
                            this.release = new Version(rel);
                    case "version" -> next = version ->
                            this.versions.add(new Version(version));
                    case "lastUpdated" -> next = lastUpd ->
                            this.lastUpdated = LocalDateTime.parse(
                                    lastUpd,
                                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                            );
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                next.accept(characterBuffer.toString().trim());
                characterBuffer.setLength(0);
                next = __ -> {};
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                characterBuffer.append(ch, start, length);
            }
        };

        var factory = SAXParserFactory.newDefaultInstance();
        try {
            var saxParser = factory.newSAXParser();
            saxParser.parse(
                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                    handler
            );
        } catch (ParserConfigurationException | SAXException e) {
            LOGGER.error("Failed to parse XML", e);
        } catch (IOException e) {
            LOGGER.error("Failed to read XML", e);
        }

        return new MavenMetadata(
                handler.group,
                handler.artifact,
                handler.latest,
                handler.release,
                handler.versions,
                handler.lastUpdated
        );
    }

    Optional<Version> resolveVersionRange(VersionRange range) {
        return this.versions.stream()
                .sorted(Comparator.reverseOrder())
                .filter(range::includes)
                .findFirst();
    }
}
