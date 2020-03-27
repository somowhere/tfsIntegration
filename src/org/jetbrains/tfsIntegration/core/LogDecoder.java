//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.ChunkedInputStream;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class LogDecoder {
    private LogDecoder() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: LogDecoder idea.log output.log");
        } else {
            FileInputStream is = new FileInputStream(args[0]);

            try {
                OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(args[1]));

                try {
                    List<LogDecoder.Entry> entries = decode(is);
                    Iterator var4 = entries.iterator();

                    while(var4.hasNext()) {
                        LogDecoder.Entry entry = (LogDecoder.Entry)var4.next();
                        os.write(entry.getText());
                    }
                } finally {
                    os.close();
                }
            } finally {
                is.close();
            }

        }
    }

    public static List<LogDecoder.Entry> decode(InputStream inputStream) throws IOException {
        List<LogDecoder.Entry> result = new ArrayList();
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        Map<LogDecoder.MessageType, String> session = null;
        LogDecoder.MessageType currentType = null;
        StringBuilder accumulated = new StringBuilder();

        String line;
        while((line = r.readLine()) != null) {
            LogDecoder.MessageType lineType = null;
            LogDecoder.MessageType[] var8 = LogDecoder.MessageType.values();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                LogDecoder.MessageType type = var8[var10];
                if (line.contains(type.getPrefix())) {
                    lineType = type;
                    int beginIndex = line.indexOf(type.getPrefix()) + type.getPrefix().length();
                    int endIndex = line.lastIndexOf(type.getSuffix());
                    line = line.substring(beginIndex, endIndex);
                    break;
                }
            }

            if (lineType == null) {
                result.add(new LogDecoder.UnknownEntry(line));
            } else {
                if (session == null) {
                    session = new HashMap();
                } else if (session.containsKey(currentType)) {
                    result.add(new LogDecoder.SessionEntry(session));
                    session = new HashMap();
                }

                if (currentType != null && lineType != currentType) {
                    byte[] unescaped = unescape(accumulated.toString());
                    String unzipped = null;
                    if (currentType == LogDecoder.MessageType.ContentIn) {
                        unzipped = tryUnzip(unescaped);
                    }

                    String display = unzipped != null ? unzipped : new String(unescaped);
                    display = tryPrettyPrintXml(display);
                    session.put(currentType, display);
                    accumulated = new StringBuilder();
                }

                accumulated.append(line);
                currentType = lineType;
            }
        }

        if (session != null) {
            result.add(new LogDecoder.SessionEntry(session));
        }

        return result;
    }

    private static byte[] unescape(String escaped) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int i = 0;

        while(i < escaped.length()) {
            if (escaped.startsWith("[\\r]", i)) {
                os.write(13);
                i += "[\\r]".length();
            } else if (escaped.startsWith("[\\n]", i)) {
                os.write(10);
                i += "[\\n]".length();
            } else if (escaped.startsWith("[0x", i)) {
                int closingBracket = escaped.indexOf(93, i);
                String hex = escaped.substring(i + 3, closingBracket);
                byte ch = (byte)Integer.parseInt(hex, 16);
                os.write(ch);
                i = closingBracket + 1;
            } else {
                os.write(escaped.charAt(i));
                ++i;
            }
        }

        return os.toByteArray();
    }

    private static boolean equal(byte[] a1, int a1start, byte[] a2, int length) {
        for(int i = 0; i < length; ++i) {
            if (a1[a1start + i] != a2[i]) {
                return false;
            }
        }

        return true;
    }

    private static String tryUnzip(byte[] zipped) {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(zipped);
            ChunkedInputStream cs = new ChunkedInputStream(is);
            GZIPInputStream zs = new GZIPInputStream(cs);
            BufferedReader r = new BufferedReader(new InputStreamReader(zs));
            StringBuilder result = new StringBuilder();

            String line;
            while((line = r.readLine()) != null) {
                result.append(line).append("\n");
            }

            return result.toString();
        } catch (IOException var7) {
            if (!"Not in GZIP format".equals(var7.getMessage())) {
                var7.printStackTrace();
            }

            return null;
        }
    }

    private static String tryPrettyPrintXml(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource sourceXML = new InputSource(new StringReader(xml));
            Document xmlDoc = db.parse(sourceXML);
            Element e = xmlDoc.getDocumentElement();
            e.normalize();
            StringWriter result = new StringWriter();
            OutputFormat format = new OutputFormat(xmlDoc);
            format.setIndenting(true);
            format.setIndent(2);
            XMLSerializer serializer = new XMLSerializer(result, format);
            serializer.serialize(xmlDoc);
            return result.toString();
        } catch (Exception var9) {
            return xml;
        }
    }

    private static class UnknownEntry implements LogDecoder.Entry {
        private final String myText;

        public UnknownEntry(String text) {
            this.myText = text;
        }

        @NotNull
        public String getText() {
            String var10000 = this.myText + "\n";

            return var10000;
        }
    }

    private static class SessionEntry implements LogDecoder.Entry {
        private final Map<LogDecoder.MessageType, String> mySession;

        public SessionEntry(Map<LogDecoder.MessageType, String> session) {
            this.mySession = session;
        }

        @NotNull
        public String getText() {
            StringBuilder s = new StringBuilder();
            LogDecoder.MessageType[] var2 = LogDecoder.MessageType.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                LogDecoder.MessageType t = var2[var4];
                if (this.mySession.containsKey(t)) {
                    String[] lines = ((String)this.mySession.get(t)).split("\n");
                    String[] var7 = lines;
                    int var8 = lines.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        String line = var7[var9];
                        if (line.length() > 0 && !"\n".equals(line.trim())) {
                            s.append(t.getPrefix()).append(line).append("\n");
                        }
                    }
                }
            }

            String var10000 = s.toString();

            return var10000;
        }
    }

    private static enum MessageType {
        HeaderOut("header - >> \"", "\""),
        ContentOut("content - >> \"", "\""),
        HeaderIn("header - << \"", "\""),
        ContentIn("content - << \"", "\"");

        private final String myPrefix;
        private final String mySuffix;

        private MessageType(String prefix, String suffix) {
            this.myPrefix = prefix;
            this.mySuffix = suffix;
        }

        public String getPrefix() {
            return this.myPrefix;
        }

        public String getSuffix() {
            return this.mySuffix;
        }
    }

    private interface Entry {
        String getText();
    }
}
