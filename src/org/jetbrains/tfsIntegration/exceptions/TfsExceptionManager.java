//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.exceptions;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLHandshakeException;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;

public class TfsExceptionManager {
    private static final String TRANSPORT_ERROR_MESSAGE = "Transport error: ";
    private static final Map<String, Class<?>> ourExceptionsBySubcodes = new HashMap();

    public TfsExceptionManager() {
    }

    @Nullable
    private static TfsException createTfsExceptionFromThrowable(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            return new ConnectionFailedException(throwable);
        } else if (throwable instanceof UnknownHostException) {
            return new HostNotFoundException(throwable);
        } else if (throwable instanceof NoHttpResponseException) {
            return new HostNotFoundException(throwable);
        } else if (throwable instanceof SSLHandshakeException) {
            return new SSLConnectionException((SSLHandshakeException)throwable);
        } else if (throwable instanceof SOAPProcessingException) {
            return new ConnectionFailedException(throwable, TFSBundle.message("invalid.soap.response", new Object[0]));
        } else if (throwable instanceof SocketException) {
            return new ConnectionFailedException(throwable);
        } else {
            return !(throwable instanceof SocketTimeoutException) && !(throwable instanceof ConnectTimeoutException) ? null : new ConnectionTimeoutException(throwable);
        }
    }

    @Nullable
    private static TfsException createTfsExceptionFromAxisFault(AxisFault axisFault) {
        TfsException result = createTfsExceptionFromThrowable(axisFault.getCause());
        if (result != null) {
            return result;
        } else {
            SOAPFaultCode code = axisFault.getFaultCodeElement();
            if (code != null) {
                SOAPFaultSubCode subCode = code.getSubCode();
                if (subCode != null) {
                    SOAPFaultValue subcodeValue = subCode.getValue();
                    if (subcodeValue != null) {
                        String subcodeText = subcodeValue.getText();
                        if (subcodeText != null) {
                            Class<?> exceptionClass = (Class)ourExceptionsBySubcodes.get(subcodeText);
                            if (exceptionClass != null) {
                                try {
                                    return (TfsException)exceptionClass.getConstructor(AxisFault.class).newInstance(axisFault);
                                } catch (Exception var8) {
                                }
                            }
                        }
                    }
                }
            }

            int errorCode = getTransportErrorCode(axisFault);
            return (TfsException)(errorCode != -1 ? createHttpTransportErrorException(errorCode, axisFault) : new UnknownException(axisFault));
        }
    }

    public static int getTransportErrorCode(AxisFault axisFault) {
        if (axisFault.getMessage() != null && axisFault.getMessage().startsWith("Transport error: ")) {
            String[] tokens = axisFault.getMessage().substring("Transport error: ".length()).split(" ");
            if (tokens.length > 0) {
                try {
                    return Integer.parseInt(tokens[0]);
                } catch (NumberFormatException var3) {
                }
            }
        }

        return -1;
    }

    public static TfsException processException(Exception e) {
        Object result;
        if (e instanceof AxisFault) {
            result = createTfsExceptionFromAxisFault((AxisFault)e);
        } else {
            result = createTfsExceptionFromThrowable(e);
        }

        if (e instanceof TfsException) {
            result = (TfsException)e;
        }

        if (result == null) {
            TFSVcs.LOG.warn("Unknown exception", e);
            result = new UnknownException(e);
        }

        return (TfsException)result;
    }

    public static TfsException createHttpTransportErrorException(int errorCode, AxisFault axisFault) {
        switch(errorCode) {
            case 401:
                return new UnauthorizedException(axisFault);
            case 403:
                return new ForbiddenException(axisFault);
            case 404:
                return new HostNotApplicableException(axisFault);
            case 407:
                return new TfsException(TFSBundle.message("proxy.auth.failed", new Object[0]));
            case 502:
                return new HostNotFoundException(axisFault);
            default:
                return new ConnectionFailedException(axisFault, errorCode);
        }
    }

    static {
        ourExceptionsBySubcodes.put("WorkspaceNotFoundException", WorkspaceNotFoundException.class);
        ourExceptionsBySubcodes.put("IllegalIdentityException", IllegalIdentityException.class);
        ourExceptionsBySubcodes.put("IdentityNotFoundException", IdentityNotFoundException.class);
        ourExceptionsBySubcodes.put("ItemNotFoundException", ItemNotFoundException.class);
        ourExceptionsBySubcodes.put("InvalidPathException", InvalidPathException.class);
    }
}
