package org.skyscreamer.nevado.jms;

import org.apache.commons.lang3.StringUtils;
import org.skyscreamer.nevado.jms.message.JMSXProperty;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nevado's implementation of ConnectionMetaData.  Uses the manifest to populate provider-specific information.
 *
 * Version parsing logic borrowed from Andrew Kutz's net.sf.nvn.commons.Version, which is licensed under Apache 2.0.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class NevadoConnectionMetaData implements ConnectionMetaData {
    private static Pattern STD_VERSION_PATT =
            Pattern.compile("^([^\\d]*?)(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\.(\\d+))?(.*)$");

    private static final NevadoConnectionMetaData INSTANCE = new NevadoConnectionMetaData();

    private final Package _package = getClass().getPackage();
    private final String _provider = _package.getImplementationTitle();
    private final ProviderVersion _version = new ProviderVersion(_package.getImplementationVersion());

    public static NevadoConnectionMetaData getInstance() {
        return INSTANCE;
    }

    public String getJMSVersion() throws JMSException {
        return "1.1";
    }

    public int getJMSMajorVersion() throws JMSException {
        return 1;
    }

    public int getJMSMinorVersion() throws JMSException {
        return 1;
    }

    public String getJMSProviderName() throws JMSException {
        return _provider;
    }

    public String getProviderVersion() throws JMSException {
        return _version.getVersion();
    }

    public int getProviderMajorVersion() throws JMSException {
        return _version.getMajorVersion();
    }

    public int getProviderMinorVersion() throws JMSException {
        return _version.getMinorVersion();
    }

    public Enumeration getJMSXPropertyNames() throws JMSException {
        List<JMSXProperty> propertyList = JMSXProperty.getSupportedProperties();
        Vector<String> propertyNames = new Vector<String>(propertyList.size());
        for(JMSXProperty property : propertyList) {
            propertyNames.add(property.name());
        }
        return propertyNames.elements();
    }

    private class ProviderVersion {
        private String _version;
        private int _majorVersion;
        private int _minorVersion;

        private ProviderVersion(String version) {
            _version = version;
            if (_version != null && _version.indexOf(".") > 0) {
                Matcher m = STD_VERSION_PATT.matcher(version);

                if (!m.find())
                {
                    // No parsable version
                    return;
                }

                if (StringUtils.isNotEmpty(m.group(2)))
                {
                    _majorVersion = Integer.valueOf(m.group(2));
                }

                if (StringUtils.isNotEmpty(m.group(3)))
                {
                    _minorVersion = Integer.valueOf(m.group(3));
                }
            }
        }

        public String getVersion() {
            return _version;
        }

        public int getMajorVersion() {
            return _majorVersion;
        }

        public int getMinorVersion() {
            return _minorVersion;
        }
    }
}
