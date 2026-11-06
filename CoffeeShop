import java.text.Normalizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

/** Pedido inmutable de un cliente. */
class Pedido {
    private final String nombreCliente;
    private final List<String> productos;
    private final String detalle;
    private final String mesero;

    public Pedido(String nombreCliente, List<String> productos, String detalle, String mesero) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }
        this.nombreCliente = nombreCliente;
        this.productos = List.copyOf(productos); // copia defensiva e inmodificable
        this.detalle = Objects.requireNonNull(detalle, "El destino es obligatorio.");
        this.mesero = Objects.requireNonNull(mesero, "El mesero es obligatorio.");
    }

    @Override
    public String toString() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("Cliente: ").append(nombreCliente)
              .append(" | Destino: ").append(detalle)
              .append(" | Atendido por: ").append(mesero).append("\n");
        for (String prod : productos) {
            ticket.append("    - ").append(prod).append("\n");
        }
        return ticket.toString();
    }
}

public final class CoffeeShop {

    private static final String CANCELAR = "0";

    /**
     * Requisito 1. TDA: Cola (FIFO). Implementación JCF: ArrayDeque.
     * offer() inserta al final en O(1) amortizado; poll() extrae del frente en O(1).
     * Se descarta ArrayList porque remove(0) desplaza todos los elementos: O(n).
     */
    private final Queue<Pedido> pedidos = new ArrayDeque<>();

    /**
     * Requisito 2. TDA: Conjunto. Implementación JCF: HashSet.
     * Garantiza unicidad (add() devuelve false ante duplicados) y contains() es O(1)
     * en promedio. No se usa TreeSet porque el menú no necesita orden (O(log n)).
     * Contiene solo los nombres normalizados de los productos: un elemento por producto.
     */
    private final Set<String> menuProductos = new HashSet<>();

    /** Índice auxiliar (no es el menú): código normalizado ("p1") -> nombre normalizado ("latte"). */
    private final Map<String, String> codigoANombre = new HashMap<>();

    /** Solo presentación: nombre normalizado -> "P1 (Latte)", en orden de inserción. */
    private final Map<String, String> etiquetas = new LinkedHashMap<>();

    /**
     * Requisito 3. TDA: Mapa (clave -> valor). Implementación JCF: HashMap.
     * get(id) es O(1) en promedio. No se usa TreeMap porque no se requiere orden de IDs (O(log n)).
     */
    private final Map<Integer, String> empleados = new HashMap<>();

    public CoffeeShop() {
        cargarDatosIniciales();
    }

    // ===================== Lógica del negocio (sin consola) =====================

    private static String normalizarTexto(String texto) {
        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        return limpio.toLowerCase().trim();
    }

    private void cargarDatosIniciales() {
        agregarProductoAMenu("P1", "Latte");
        agregarProductoAMenu("P2", "Cappuccino");
        agregarProductoAMenu("P3", "Muffin");
        agregarProductoAMenu("P4", "Cafe");
        agregarProductoAMenu("P5", "Granizado de Cafe");

        agregarEmpleado(101, "Nicole Valentina Quevedo Franco");
        agregarEmpleado(102, "Matias Espinal Jaramillo");
        agregarEmpleado(103, "Cristian Camilo Londoño Vargas");
        agregarEmpleado(104, "Santiago Zapata Zuleta");
    }

    /** Agrega un producto al menú. Lanza IllegalArgumentException si es nulo, vacío o repetido. */
    public void agregarProductoAMenu(String codigo, String producto) {
        if (codigo == null || codigo.isBlank() || producto == null || producto.isBlank()) {
            throw new IllegalArgumentException("Codigo y nombre del producto son obligatorios.");
        }
        String codLimpio = normalizarTexto(codigo);
        String nombreLimpio = normalizarTexto(producto);

        if (codigoANombre.containsKey(codLimpio) || !menuProductos.add(nombreLimpio)) {
            throw new IllegalArgumentException("Producto o codigo duplicado: " + codigo + " / " + producto);
        }
        codigoANombre.put(codLimpio, nombreLimpio);
        etiquetas.put(nombreLimpio, codigo + " (" + producto + ")");
    }

    /**
     * Busca un producto por código o nombre en O(1) promedio.
     * @return la etiqueta del producto, p. ej. "P1 (Latte)"
     * @throws NoSuchElementException si no existe en el menú
     */
    public String buscarProducto(String entrada) {
        String clave = normalizarTexto(Objects.requireNonNull(entrada));
        String nombre = codigoANombre.getOrDefault(clave, clave);
        if (!menuProductos.contains(nombre)) {
            throw new NoSuchElementException("El producto '" + entrada + "' no existe en el menu.");
        }
        return etiquetas.get(nombre);
    }

    private void validarIdNuevo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser un entero positivo.");
        }
        if (empleados.containsKey(id)) {
            throw new IllegalArgumentException("El ID " + id + " ya existe.");
        }
    }

    /** Registra un empleado. IDs positivos (el 0 está reservado para cancelar). */
    public void agregarEmpleado(int id, String nombre) {
        validarIdNuevo(id);
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del empleado no puede estar vacio.");
        }
        if (empleados.putIfAbsent(id, nombre.trim()) != null) {
            throw new IllegalArgumentException("El ID " + id + " ya existe.");
        }
    }

    /**
     * Búsqueda instantánea del nombre por ID: una sola llamada a get(), O(1) promedio.
     * @throws NoSuchElementException si el ID no existe
     */
    public String buscarNombreEmpleado(int id) {
        String nombre = empleados.get(id);
        if (nombre == null) {
            throw new NoSuchElementException("No existe un empleado con ID " + id + ".");
        }
        return nombre;
    }

    /** Retira un empleado. @throws NoSuchElementException si el ID no existe */
    public String retirarEmpleado(int id) {
        String nombre = empleados.remove(id);
        if (nombre == null) {
            throw new NoSuchElementException("No existe un empleado con ID " + id + ".");
        }
        return nombre;
    }

    /** Encola un pedido al final: O(1) amortizado. */
    public void registrarPedido(Pedido pedido) {
        pedidos.offer(Objects.requireNonNull(pedido, "No se puede registrar un pedido nulo."));
    }

    /** Extrae el pedido más antiguo: O(1). Devuelve null si la cola está vacía. */
    public Pedido atenderSiguiente() {
        return pedidos.poll();
    }

    // ===================== Interfaz de consola =====================

    private static boolean esCancelacion(String entrada) {
        if (CANCELAR.equals(entrada)) {
            System.out.println("Operacion cancelada. Volviendo al menu...");
            return true;
        }
        return false;
    }

    private void mostrarEmpleados(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        for (Map.Entry<Integer, String> emp : empleados.entrySet()) {
            System.out.println("ID: " + emp.getKey() + " -> " + emp.getValue());
        }
    }

    public void iniciarSistema() {
        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        System.out.println("=== BIENVENIDO AL SISTEMA DE COFFEE SHOP ===");

        try {
            while (ejecutando) {
                System.out.println("\n[Monitor] Pedidos en cola de preparacion: " + pedidos.size());
                System.out.println("1. Registrar nuevo pedido del cliente");
                System.out.println("2. Preparar (Atender) siguiente pedido");
                System.out.println("3. Ver lista de pedidos en espera");
                System.out.println("4. Gestionar personal (Agregar/Quitar meseros)");
                System.out.println("5. Salir del sistema");
                System.out.print("Seleccione una opcion: ");

                switch (scanner.nextLine().trim()) {
                    case "1": registrarPedidoInteractivo(scanner); break;
                    case "2": atenderSiguientePedido(); break;
                    case "3": verPedidosPendientes(); break;
                    case "4": gestionarMeseros(scanner); break;
                    case "5":
                        System.out.println("Cerrando sistema... Buen turno!");
                        ejecutando = false;
                        break;
                    default: System.out.println("Error: Opcion no valida.");
                }
            }
        } catch (NoSuchElementException e) {
            // nextLine() sin más entrada (Ctrl+D / Ctrl+Z o fin de archivo)
            System.out.println("\nEntrada finalizada. Cerrando sistema...");
        }
        scanner.close();
    }

    private void gestionarMeseros(Scanner scanner) {
        System.out.println("\n--- GESTION DE PERSONAL ---");
        System.out.println("1 - Contratar (Agregar) nuevo mesero");
        System.out.println("2 - Despedir (Quitar) mesero");
        System.out.println("3 - Volver al menu principal");
        System.out.print("-> ");

        String opcion = scanner.nextLine().trim();

        try {
            if (opcion.equals("1")) {
                System.out.print("\nIngrese el ID numerico (positivo) del nuevo mesero: ");
                int nuevoId = Integer.parseInt(scanner.nextLine().trim());
                validarIdNuevo(nuevoId); // falla antes de pedir el nombre
                System.out.print("Ingrese el nombre completo del mesero: ");
                agregarEmpleado(nuevoId, scanner.nextLine());
                System.out.println("[EXITO] Mesero agregado.");
            } else if (opcion.equals("2")) {
                mostrarEmpleados("NOMINA ACTUAL");
                System.out.print("\nIngrese el ID del mesero a retirar: ");
                int idEliminar = Integer.parseInt(scanner.nextLine().trim());
                System.out.println("[EXITO] Mesero retirado: " + retirarEmpleado(idEliminar));
            } else if (opcion.equals("3")) {
                System.out.println("Volviendo al menu principal...");
            } else {
                System.out.println("Error: Opcion no valida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El ID debe ser numerico.");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarPedidoInteractivo(Scanner scanner) {
        if (empleados.isEmpty()) {
            System.out.println("Error: No hay meseros. Vaya a Gestionar Personal.");
            return;
        }
        mostrarEmpleados("MESEROS DISPONIBLES");

        String nombreEmpleado;
        while (true) {
            System.out.print("\nIngrese su ID de empleado (0 = cancelar): ");
            String input = scanner.nextLine().trim();
            if (esCancelacion(input)) return;
            try {
                nombreEmpleado = buscarNombreEmpleado(Integer.parseInt(input));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: El ID debe ser un numero.");
            } catch (NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        String nombreCliente;
        while (true) {
            System.out.println("\n--- DATOS DEL CLIENTE ---");
            System.out.print("Ingrese el nombre del cliente (0 = cancelar): ");
            nombreCliente = scanner.nextLine().trim();
            if (esCancelacion(nombreCliente)) return;
            if (!nombreCliente.isEmpty()) break;
            System.out.println("Error: El nombre no puede estar vacio.");
        }

        List<String> productosDelCliente = new ArrayList<>();
        boolean agregandoProductos = true;

        while (agregandoProductos) {
            System.out.println("\n--- MENU DE PRODUCTOS ---");
            etiquetas.values().forEach(System.out::println);
            System.out.println("0 - Cancelar pedido");

            while (true) {
                System.out.print("\nIngrese el CODIGO o NOMBRE del producto: ");
                String producto = scanner.nextLine().trim();
                if (esCancelacion(producto)) return;
                if (producto.isEmpty()) {
                    System.out.println("Error: No puede estar vacio.");
                    continue;
                }
                try {
                    productosDelCliente.add(buscarProducto(producto));
                    break;
                } catch (NoSuchElementException e) {
                    System.out.println("Error: " + e.getMessage() + " Verifique el codigo o nombre.");
                }
            }

            while (true) {
                System.out.print("\n¿Desea agregar otro producto? (S/N, 0 = cancelar): ");
                String continuar = scanner.nextLine().trim().toUpperCase();
                if (esCancelacion(continuar)) return;
                if (continuar.equals("S")) break;
                if (continuar.equals("N")) { agregandoProductos = false; break; }
                System.out.println("Error: Responda S o N.");
            }
        }

        String detalle = null;
        while (detalle == null) {
            System.out.println("\n--- DESTINO DEL PEDIDO ---");
            System.out.println("1 - Consumir en el local (Mesa)");
            System.out.println("2 - Para llevar");
            System.out.print("Seleccione (1 o 2, 0 = cancelar): ");
            String opDestino = scanner.nextLine().trim();
            if (esCancelacion(opDestino)) return;

            if (opDestino.equals("1")) {
                while (detalle == null) {
                    System.out.print("\nIngrese el numero de mesa (1 al 9, 0 = cancelar): ");
                    String inputMesa = scanner.nextLine().trim();
                    if (esCancelacion(inputMesa)) return;
                    try {
                        int numMesa = Integer.parseInt(inputMesa);
                        if (numMesa >= 1 && numMesa <= 9) {
                            detalle = "Mesa " + numMesa;
                        } else {
                            System.out.println("Error: El local solo tiene 9 mesas.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Ingrese un numero valido.");
                    }
                }
            } else if (opDestino.equals("2")) {
                detalle = "Para llevar";
            } else {
                System.out.println("Error: Seleccione 1 o 2.");
            }
        }

        registrarPedido(new Pedido(nombreCliente, productosDelCliente, detalle, nombreEmpleado));
        System.out.println("\n[EXITO] Pedido registrado correctamente en la cola de preparacion.");
    }

    private void atenderSiguientePedido() {
        Pedido pedidoAtendido = atenderSiguiente();
        System.out.println("\n--- ZONA DE PREPARACION ---");
        if (pedidoAtendido != null) {
            System.out.println("Preparando pedido:\n" + pedidoAtendido);
        } else {
            System.out.println("No hay pedidos pendientes en la cola.");
        }
    }

    private void verPedidosPendientes() {
        System.out.println("\n--- PANTALLA DE COCINA: PEDIDOS EN ESPERA ---");
        if (pedidos.isEmpty()) {
            System.out.println("No hay ordenes pendientes.");
            return;
        }
        int contador = 1;
        for (Pedido p : pedidos) {
            System.out.println("\n[Orden #" + contador++ + "]");
            System.out.println(p);
        }
    }

    public static void main(String[] args) {
        new CoffeeShop().iniciarSistema();
    }
}
