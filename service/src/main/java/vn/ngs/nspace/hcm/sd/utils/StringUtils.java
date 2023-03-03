package vn.ngs.nspace.hcm.sd.utils;

import org.apache.commons.lang.ObjectUtils;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils extends org.apache.commons.lang.StringUtils {

    public static void addNotEmpty(StringBuilder sb, String str) {
        if (sb == null || sb.length() == 0) {
            return;
        }
        sb.append(str);
    }

    public static String convertCollectionToString(Collection<?> collection) {
        if (collection.size() < 2) {
            Iterator<?> iterator = collection.iterator();
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return ObjectUtils.toString(first);
            }
        }
        return StringUtils.join(collection, ",");
    }
}
