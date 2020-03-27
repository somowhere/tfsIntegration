//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.version;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter;

public class ChangesetVersionSpec extends VersionSpecBase {
    private final int changeSetId;

    public int getChangeSetId() {
        return this.changeSetId;
    }

    public ChangesetVersionSpec(int changeSetId) {
        this.changeSetId = changeSetId;
    }

    protected void writeAttributes(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
        writeVersionAttribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", xmlWriter);
        writeVersionAttribute("", "xsi:type", "ChangesetVersionSpec", xmlWriter);
        writeVersionAttribute("", "cs", Integer.toString(this.changeSetId), xmlWriter);
    }

    public String getPresentableString() {
        return String.valueOf(this.changeSetId);
    }
}
