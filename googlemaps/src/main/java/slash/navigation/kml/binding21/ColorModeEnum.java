//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.10.07 at 09:27:50 PM CEST 
//


package slash.navigation.kml.binding21;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for colorModeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="colorModeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="random"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
public enum ColorModeEnum {

    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("random")
    RANDOM("random");
    private final String value;

    ColorModeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorModeEnum fromValue(String v) {
        for (ColorModeEnum c: ColorModeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
