package com.example.wiberdriver.utils

object Const {
    const val TAG = "socket"
    const val placeholder = "placeholder"

    /**
     * `im` in address is the endpoint configured in server.
     * If you are using AVD provided by Android Studio, you should uncomment the upper address.
     * If you are using Genymotion, nothing else to do.
     * If you are using your own phone, just change the server address and port.
     */
    const val address = "ws://10.0.2.2:8080/im/websocket"
//	public static final String address = "ws://10.0.3.2:8080/im/websocket";

    //	public static final String address = "ws://10.0.3.2:8080/im/websocket";
    const val broadcast = "/broadcast"
    const val broadcastResponse = "/b"

    // replace {@code placeholder} with group id
    const val group = "/group/$placeholder"
    const val groupResponse = "/g/$placeholder"
    const val chat = "/chat"

    // replace {@code placeholder} with user id
    const val chatResponse = "/user/$placeholder/msg"
}