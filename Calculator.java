import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.util.*;

public class Calculator {
  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/", Calculator::handle);
    server.start();
    System.out.println("Calculadora disponível na porta 8000");
  }

  static void handle(HttpExchange e) throws IOException {
    Map<String, String> params = query(e.getRequestURI().getRawQuery());
    byte[] body = page(calculate(params)).getBytes(StandardCharsets.UTF_8);
    e.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
    e.sendResponseHeaders(200, body.length);
    try (OutputStream out = e.getResponseBody()) {
      out.write(body);
    }
  }

  static Map<String, String> query(String raw) {
    Map<String, String> result = new HashMap<>();
    if (raw == null) return result;
    for (String pair : raw.split("&")) {
      String[] values = pair.split("=", 2);
      if (values.length == 2) {
        result.put(URLDecoder.decode(values[0], StandardCharsets.UTF_8),
            URLDecoder.decode(values[1], StandardCharsets.UTF_8));
      }
    }
    return result;
  }

  static String calculate(Map<String, String> params) {
    if (!params.containsKey("a") || !params.containsKey("b") || !params.containsKey("op")) {
      return "";
    }

    try {
      double a = Double.parseDouble(params.get("a"));
      double b = Double.parseDouble(params.get("b"));
      double value;
      String symbol;

      switch (params.get("op")) {
        case "add" -> { value = a + b; symbol = "+"; }
        case "subtract" -> { value = a - b; symbol = "-"; }
        case "multiply" -> { value = a * b; symbol = "*"; }
        case "divide" -> {
          if (b == 0) return "Erro: divisão por zero não é permitida.";
          value = a / b;
          symbol = "/";
        }
        default -> { return "Erro: operação inválida."; }
      }

      return "Resultado: " + format(a) + " " + symbol + " " + format(b) + " = " + format(value);
    } catch (NumberFormatException error) {
      return "Erro: informe números válidos.";
    }
  }

  static String format(double value) {
    return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
  }

  static String page(String message) {
    String safe = message.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
    return "<!doctype html><html lang='pt-BR'><head><meta charset='utf-8'>"
        + "<title>Calculadora Docker</title></head>"
        + "<body style='font-family:Arial;max-width:600px;margin:40px auto;padding:24px;background:#f4f7fb'>"
        + "<main style='background:white;padding:28px;border-radius:12px'>"
        + "<h1>Calculadora Docker</h1><p>Aplicação Java executando em um container.</p>"
        + "<form method='get'><label>Primeiro número<br>"
        + "<input name='a' type='number' step='any' required></label><br><br>"
        + "<label>Segundo número<br><input name='b' type='number' step='any' required></label><br><br>"
        + "<label>Operação <select name='op'><option value='add'>Somar</option>"
        + "<option value='subtract'>Subtrair</option><option value='multiply'>Multiplicar</option>"
        + "<option value='divide'>Dividir</option></select></label><br><br>"
        + "<button type='submit'>Calcular</button></form><h2>" + safe
        + "</h2></main></body></html>";
  }
}
