package com.vmonaco.bbl;

import java.util.List;
import java.util.LinkedList;

public class SessionData {
    String identity;
    String key;
    int session_id;
    String os_name;
    String os_arch;
    String os_version;
    String locale;
    List<String> tags = new LinkedList<String>();
}
