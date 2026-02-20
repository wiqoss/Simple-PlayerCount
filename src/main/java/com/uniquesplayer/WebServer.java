package com.uniquesplayer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class WebServer {
    
    private final UniquesPlayer plugin;
    private final int port;
    private HttpServer server;
    private static final String HTML_TEMPLATE;
    private static final String JSON_TEMPLATE = "{\"unique_players\":%d,\"status\":\"success\"}";
    
    static {
        HTML_TEMPLATE = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Unique Players</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700;800&display=swap" rel="stylesheet">
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:'Inter',sans-serif;background:#0a0a0a;min-height:100vh;display:flex;justify-content:center;align-items:center;color:#fff;overflow:hidden}
.bg{position:fixed;inset:0;background:radial-gradient(ellipse at 20%% 20%%,rgba(120,119,198,.15) 0%%,transparent 50%%),radial-gradient(ellipse at 80%% 80%%,rgba(74,222,128,.1) 0%%,transparent 50%%);z-index:-2}
.grid{position:fixed;inset:0;background-image:linear-gradient(rgba(255,255,255,.02) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,.02) 1px,transparent 1px);background-size:60px 60px;z-index:-1}
.card{text-align:center;padding:60px 80px;background:rgba(255,255,255,.03);backdrop-filter:blur(20px);border:1px solid rgba(255,255,255,.08);border-radius:32px;box-shadow:0 20px 50px rgba(0,0,0,.4)}
.badge{display:inline-flex;align-items:center;gap:8px;padding:8px 16px;background:rgba(74,222,128,.1);border:1px solid rgba(74,222,128,.2);border-radius:100px;font-size:13px;font-weight:500;color:#4ade80;margin-bottom:30px}
.badge::before{content:'';width:8px;height:8px;background:#4ade80;border-radius:50%%;animation:pulse 2s infinite}
@keyframes pulse{0%%,100%%{opacity:1;transform:scale(1)}50%%{opacity:.5;transform:scale(.8)}}
h1{font-size:16px;font-weight:500;color:rgba(255,255,255,.5);text-transform:uppercase;letter-spacing:4px;margin-bottom:20px}
.count{font-size:140px;font-weight:800;background:linear-gradient(135deg,#fff 0%%,#4ade80 50%%,#2dd4bf 100%%);-webkit-background-clip:text;-webkit-text-fill-color:transparent;background-clip:text;line-height:1;animation:glow 3s infinite}
@keyframes glow{0%%,100%%{filter:drop-shadow(0 0 20px rgba(74,222,128,.3))}50%%{filter:drop-shadow(0 0 40px rgba(74,222,128,.5))}}
.label{font-size:18px;color:rgba(255,255,255,.6);margin-top:20px}
.stats{display:flex;justify-content:center;gap:40px;margin-top:40px;padding-top:30px;border-top:1px solid rgba(255,255,255,.06)}
.stat-value{font-size:24px;font-weight:700}
.stat-label{font-size:12px;color:rgba(255,255,255,.4);text-transform:uppercase;letter-spacing:1px;margin-top:4px}
.orb{position:fixed;border-radius:50%%;filter:blur(80px);opacity:.4;animation:float 20s infinite;z-index:-1}
.o1{width:400px;height:400px;background:#4ade80;top:-200px;right:-100px}
.o2{width:300px;height:300px;background:#2dd4bf;bottom:-150px;left:-100px;animation-delay:-10s}
@keyframes float{0%%,100%%{transform:translate(0,0)}50%%{transform:translate(-20px,20px)}}
</style>
</head>
<body>
<div class="bg"></div>
<div class="grid"></div>
<div class="orb o1"></div>
<div class="orb o2"></div>
<div class="card">
<div class="badge">Live</div>
<h1>Unique Players</h1>
<div class="count" id="c">0</div>
<div class="label">Players joined</div>
<div class="stats">
<div><div class="stat-value" id="t">0</div><div class="stat-label">Total</div></div>
<div><div class="stat-value">24/7</div><div class="stat-label">Uptime</div></div>
</div>
</div>
<script>
const n=%d,c=document.getElementById('c'),t=document.getElementById('t');
let s=performance.now();
(function a(now){
const p=Math.min((now-s)/1500,1),v=Math.floor(n*(1-Math.pow(1-p,3)));
c.textContent=t.textContent=v.toLocaleString();
p<1?requestAnimationFrame(a):(c.textContent=t.textContent=n.toLocaleString());
})(s);
</script>
</body>
</html>""";
    }
    
    public WebServer(UniquesPlayer plugin, int port) {
        this.plugin = plugin;
        this.port = port;
    }
    
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new MainHandler());
            server.createContext("/api", new ApiHandler());
            server.setExecutor(Executors.newFixedThreadPool(2));
            server.start();
            plugin.getLogger().info("Web server started on port " + port);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to start web server: " + e.getMessage());
        }
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    private class MainHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            byte[] resp = String.format(HTML_TEMPLATE, plugin.getUniquePlayerCount())
                    .getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/html;charset=UTF-8");
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            ex.sendResponseHeaders(200, resp.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(resp);
            }
        }
    }
    
    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            byte[] resp = String.format(JSON_TEMPLATE, plugin.getUniquePlayerCount())
                    .getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "application/json");
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            ex.sendResponseHeaders(200, resp.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(resp);
            }
        }
    }
}
