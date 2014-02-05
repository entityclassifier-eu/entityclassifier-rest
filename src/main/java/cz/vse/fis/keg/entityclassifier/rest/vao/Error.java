/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vse.fis.keg.entityclassifier.rest.vao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author Milan Dojchinovski <milan@dojchinovski.,mk>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Error {
    
    @XmlValue
    private String message;    
    @XmlAttribute(name="code")
    private int code;

    /**
     * @return the text
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param text the text to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }
    
}
