package acp;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import acp.utils.FieldConfig;

class NodeInfo {
  private Node node;
  private String title;

  NodeInfo(Node vNode) {
    setNode(vNode);
  }

  Node getNode() {
    return node;
  }

  void setNode(Node vNode) {
    node = vNode;
    fillTitle();
  }

  void fillTitle() {
    String nodeName = node.getNodeName().trim();
    title = FieldConfig.getString(nodeName);
    NamedNodeMap attr = node.getAttributes();
    if (attr.getLength() > 0) {
      title += " [ ";
      for (int i = 0; i < attr.getLength(); i++) {
        Node at = attr.item(i);
        if (i > 0) {
          title += ", ";
        }
        title += at.getNodeValue();
      }
      title += " ]";
    }
  }

  public String toString() {
    return title;
  }

}
