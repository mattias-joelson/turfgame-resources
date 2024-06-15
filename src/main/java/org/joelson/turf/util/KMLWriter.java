package org.joelson.turf.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class KMLWriter extends Writer {

    private final PrintWriter out;
    private boolean hasFolder;

    public KMLWriter(String... filenameParts) throws IOException {
        out = new PrintWriter(FilesUtil.newDefaultWriter(filenameParts));
        hasFolder = false;
        writeHeader();
    }

    private void writeHeader() {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        out.println("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\" "
                + "xmlns:gx=\"http://www.google.com/kml/ext/2.2\" "
                + "xmlns:xal=\"urn:oasis:names:tc:ciq:xsdschema:xAL:2.0\">");
        out.println("  <Document>");
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        out.write(cbuf, off, len);
    }

    public void writeFolder(String name) {
        if (hasFolder) {
            out.println("    </Folder>");
        }
        out.println("    <Folder>");
        out.println("      <name>" + name + "</name>");
        hasFolder = true;
    }

    public void writePlacemark(String name, String description, double longitude, double latitude) {
        out.println("        <Placemark>");
        out.println("          <name>" + name + "</name>");
        out.println("          <description>" + description + "</description>");
        out.println("          <Point>");
        out.println("            <coordinates>" + longitude + ',' + latitude + "</coordinates>");
        out.println("          </Point>");
        out.println("        </Placemark>");
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        writerFoot();
        out.close();
    }

    private void writerFoot() {
        if (hasFolder) {
            out.println("    </Folder>");
        }
        out.println("  </Document>");
        out.println("</kml>");
    }

}
