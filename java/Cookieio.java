public class Cookieio{
    Cookie[] cookies = null;
    public Cookieio(Cookie[] cookies) throws Exception {
        this.cookies = cookies;
    }

    public String GetCookieValue(String name) {
        if(cookies != null)
            for (int i = 0; i < cookies.length; i++)
                if (name.equalsIgnoreCase(decodeString(cookies[i].getName()))) return decodeString(cookies[i].getValue());
        return "";
    }

    public String GetCookieValue(String name, String subname) {
        if(cookies != null)
            for (int i = 0; i < cookies.length; i++)
                if (name.equalsIgnoreCase(decodeString(cookies[i].getName()))){
                    String[] subkey = cookies[i].getValue().split("&");
                    if(subkey != null)
                        for (int j = 0; j < subkey.length; j++){
                            String[] subs = subkey[j].split("=");
                            if(subs.length>0)
                                if (subname.equalsIgnoreCase(decodeString(subs[0]))){
                                    if(subs.length>1)
                                        return decodeString(subs[1]);
                                    else
                                        return "";
                                }
                        }
                    return "";
                }
        return "";
    }


    // -----------------------------------------------------------------------------
    // GetCookie 该函数用于取得一个已经存在的 Cookie，以便往其中添加 SubItem
    // 如果不存在，返回一个空值的 Cookie
    // -----------------------------------------------------------------------------
    public Cookie GetCookie(String name) {

        if(cookies != null)
            for (int i = 0; i < cookies.length; i++)
                if (name.equalsIgnoreCase(decodeString(cookies[i].getName()))) return cookies[i];

        return new Cookie(name, "");
    }

    // -----------------------------------------------------------------------------
    // addSubItem 该函数用于在一个Cookie值串中添加一个"名称"=值串
    // 前提：该 Cookie 不能为 null，必须此之前定义好 Cookie 名称
    // -----------------------------------------------------------------------------
    static public Cookie addSubItem(Cookie cookie, String subname, String subvalue) {

        String   values = "";

        String[] subkey = cookie.getValue().split("&");

        if(subkey != null)
            for (int i = 0; i < subkey.length; i++){
                String[] subs = subkey[i].split("=");
                if(subs.length == 2 && !subs[0].equals("") && !subs[1].equals("") && !subname.equalsIgnoreCase(decodeString(subs[0])) ){
                    if( values.equals("") )
                        values =       subkey[i];
                    else
                        values+= "&" + subkey[i];
                }
            }

        if ( !subname.equals("") && !subvalue.equals("") )
            if( values.equals("") )
                values =       encodeString(subname) + "=" + encodeString(subvalue);
            else
                values+= "&" + encodeString(subname) + "=" + encodeString(subvalue);

        cookie.setValue(values);

        return cookie;

    }

    // -----------------------------------------------------------------------------
    // encodeString 该函数用于将字符串用%HEX/0-9/a-z/A-Z编码
    // -----------------------------------------------------------------------------
    static public String encodeString(String s) {

        byte[] source = s.getBytes();
        byte[] buffer = new byte[source.length * 3];
        int value;
        String hex;
        int j = 0;

        for (int i = 0; i < source.length; i++) {
            if ((source[i] >= 0x30 && source[i] <= 0x39) || // 0-9
                    (source[i] >= 0x41 && source[i] <= 0x5a) || // A-Z
                    (source[i] >= 0x61 && source[i] <= 0x7a))   // a-z
                buffer[j++] = source[i];
            else if(source[i] == ' ')
                buffer[j++] = '+';
            else {
                value = (source[i] >= 0)? source[i]: 256 + source[i];
                hex = Integer.toHexString(value).toUpperCase();
                if (hex.length() < 2)
                    hex = "0" + hex;

                buffer[j++] = 0x25; // '%'
                byte[] tbs = hex.getBytes();
                buffer[j++] = tbs[0];
                buffer[j++] = tbs[1];
            }
        }

        return new String(buffer, 0, j);

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // decodeString 该函数用于将用上述方法编码的字符串解码
    // -----------------------------------------------------------------------------
    static public String decodeString(String s) {

        int i = 0, j = 0;
        byte k;
        String hex;
        byte[] source = s.getBytes();

        byte[] buffer = new byte[s.length()];

        while (i < s.length())
            if (source[i] == 0x25) { // '%'
                hex = "0x" + s.substring(i + 1, i + 3);
                k = Integer.decode(hex).byteValue();

                buffer[j++] = k;
                i += 3;
            }
            else if(source[i] == '+'){
                buffer[j++] = ' ';
                i++;
            }
            else
                buffer[j++] = source[i++];

        return new String(buffer, 0, j);
    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // addItemValue 该函数用于在一个由多个"名称"=值串并以'&'连接而成的字符串中,
    // 添加一个"名称"=值串, 若已经有该名称存在, 则代替原串
    // -----------------------------------------------------------------------------
    static public String addItemValue(String s, String item, String value) {

        int istart = s.indexOf(item + "=");
        if ((istart == -1) || ((istart != 0) && (s.charAt(istart - 1) != '&')))
            if (s.equals(""))
                s = item + "=" + value;
            else
                s += "&" + item + "=" + value;
        else { // 找到该名称
            int istop = s.indexOf('&', istart);
            String srcstr; // 原串
            if (istop == -1)
                srcstr = s.substring(istart);
            else
                srcstr = s.substring(istart, istop);

            s = s.replaceFirst(srcstr, item + "=" + value);
        }

        return s;

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // getItemValue 该函数用于在一个由多个"名称"=值串并以'&'连接而成的字符串中,
    // 取得某个"名称"的值, 若不存在, 则返回空串
    // -----------------------------------------------------------------------------
    static public String getItemValue(String s, String item) {

        int istart = s.indexOf(item + "=");
        if ((istart == -1) || !((istart == 0) || (s.charAt(istart - 1) == '&')))
            s = "";
        else { // 找到该名称
            int istop = s.indexOf('&', istart);
            if (istop == -1)
                s = s.substring(istart + (item + "=").length());
            else
                s = s.substring(istart + (item + "=").length(), istop);
        }

        return s;

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // addEncodedItemValue 该函数用于在一个由多个"名称"=值串并以'&'连接而成的已编码的字符串中,
    // 添加一个编码了的"名称"=值串, 若已经有该名称存在, 则代替原串
    // -----------------------------------------------------------------------------
    static public String addEncodedItemValue(String s, String item, String value) {

        return addItemValue(s, encodeString(item), encodeString(value));

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // getEncodedItemValue 该函数用于在一个由多个"名称"=值串并以'&'连接而成的已编码的字符串中,
    // 取得某个"名称"的解码的值, 若不存在, 则返回空串
    // -----------------------------------------------------------------------------
    static public String getEncodedItemValue(String s, String item) {

        return decodeString(getItemValue(s, encodeString(item)));

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // addCookieSubItem 该函数用于在一个Cookie值串中添加一个"名称"=值串
    // -----------------------------------------------------------------------------
    static public Cookie addCookieSubItem(Cookie cookie,
                                          String mainname, String subname, String subvalue) {

        if (!mainname.equals(cookie.getName()))
            cookie = new Cookie(mainname, encodeString(subname) + "=" + encodeString(subvalue));
        else
            cookie.setValue(addItemValue(cookie.getValue(), encodeString(subname), encodeString(subvalue)));

        return cookie;

    }
    // -----------------------------------------------------------------------------

    // -----------------------------------------------------------------------------
    // getCookieSubValue 该函数用于在一个Cookie值串中取得一个"名称"的子值
    // -----------------------------------------------------------------------------
    static public String getCookieSubValue(Cookie cookie,
                                           String mainname, String subname) {

        if (!mainname.equals(cookie.getName()))
            return "";
        else
            return decodeString(getItemValue(cookie.getValue(), encodeString(subname)));

    }
}