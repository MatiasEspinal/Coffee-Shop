import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.Normalizer;

class Pedido {
    private String nombreCliente;
    private List<String> productos;
    private String detalle;
    private String mesero;

    public Pedido(String nombreCliente, List<String> productos, String detalle, String mesero) {
        this.nombreCliente = nombreCliente;
        this.productos = productos;
        this.detalle = detalle;
        this.mesero = mesero;
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

public class CoffeeShop {
    
    // TDA: Cola -> Implementación: ArrayDeque O(1)
    private Queue<Pedido> pedidos;

    // TDA: Conjunto -> Implementación: HashSet O(1)
    private Set<String> menuProductosValidador; 
    private List<String> menuProductosDisplay;   
    
    private Map<String, String> formatoProductos;

    // TDA: Mapa -> Implementación: HashMap O(1)
    private Map<Integer, String> empleados;

    public CoffeeShop() {
        this.pedidos = new ArrayDeque<>();
        this.menuProductosValidador = new HashSet<>();
        this.menuProductosDisplay = new ArrayList<>();
        this.formatoProductos = new HashMap<>();
        this.empleados = new HashMap<>();
        cargarDatosIniciales();
    }

    private String normalizarTexto(String texto) {
        String limpio = Normalizer.normalize(texto, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return limpio.toLowerCase().trim();
    }

    private void cargarDatosIniciales() {
        agregarProductoAMenu("P1", "Latte");
        agregarProductoAMenu("P2", "Cappuccino");
        agregarProductoAMenu("P3", "Muffin");
        agregarProductoAMenu("P4", "Cafe");
        agregarProductoAMenu("P5", "Granizado de Cafe");

        empleados.put(101, "Nicole Valentina Quevedo Franco");
        empleados.put(102, "Matias Espinal Jaramillo");
        empleados.put(103, "Cristian Camilo Londoño Vargas");
        empleados.put(104, "Santiago Zapata Zuleta");
    }

    private void agregarProductoAMenu(String codigo, String producto) {
        String display = codigo + " - " + producto;
        menuProductosDisplay.add(display);
        
        String codLimpio = codigo.toLowerCase();
        String prodLimpio = normalizarTexto(producto);

        menuProductosValidador.add(codLimpio); 
        menuProductosValidador.add(prodLimpio);

        String formatoBonito = codigo + " (" + producto + ")";
        formatoProductos.put(codLimpio, formatoBonito);
        formatoProductos.put(prodLimpio, formatoBonito);
    }

    public void iniciarSistema() {
        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        System.out.println("=== BIENVENIDO AL SISTEMA DE COFFEE SHOP ===");

        while (ejecutando) {
            System.out.println("\n[Monitor] Pedidos en cola de preparacion: " + pedidos.size());
            System.out.println("1. Registrar nuevo pedido del cliente");
            System.out.println("2. Preparar (Atender) siguiente pedido");
            System.out.println("3. Ver lista de pedidos en espera");
            System.out.println("4. Gestionar personal (Agregar/Quitar meseros)");
            System.out.println("5. Salir del sistema");
            System.out.print("Seleccione una opcion: ");

            String inputOpcion = scanner.nextLine().trim();

            switch (inputOpcion) {
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
        scanner.close();
    }

    private void gestionarMeseros(Scanner scanner) {
        System.out.println("\n--- GESTION DE PERSONAL ---");
        System.out.println("1 - Contratar (Agregar) nuevo mesero");
        System.out.println("2 - Despedir (Quitar) mesero");
        System.out.println("3 - Volver al menu principal");
        System.out.print("-> ");
        
        String opcion = scanner.nextLine().trim();
        
        if (opcion.equals("3")) {
            System.out.println("Volviendo al menu principal...");
            return;
        }

        if (opcion.equals("1")) {
            System.out.println("\nIngrese el ID numerico del nuevo mesero:");
            System.out.print("-> ");
            String inputId = scanner.nextLine().trim();

            try {
                int nuevoId = Integer.parseInt(inputId);
                if (empleados.containsKey(nuevoId)) {
                    System.out.println("Error: El ID " + nuevoId + " ya existe.");
                } else {
                    System.out.print("Ingrese el nombre completo del mesero: ");
                    String nuevoNombre = scanner.nextLine().trim();
                    if (!nuevoNombre.isEmpty()) {
                        empleados.put(nuevoId, nuevoNombre);
                        System.out.println("[EXITO] Mesero agregado.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: El ID debe ser numerico.");
            }
            
        } else if (opcion.equals("2")) {
            System.out.println("\n--- NOMINA ACTUAL ---");
            for (Map.Entry<Integer, String> emp : empleados.entrySet()) {
                System.out.println("ID: " + emp.getKey() + " -> " + emp.getValue());
            }
            System.out.println("\nIngrese el ID del mesero a retirar:");
            System.out.print("-> ");
            String inputId = scanner.nextLine().trim();

            try {
                int idEliminar = Integer.parseInt(inputId);
                if (empleados.containsKey(idEliminar)) {
                    String nombreDespedido = empleados.remove(idEliminar);
                    System.out.println("[EXITO] Mesero retirado: " + nombreDespedido);
                } else {
                    System.out.println("Error: ID no encontrado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: ID invalido.");
            }
        }
    }

    private void registrarPedidoInteractivo(Scanner scanner) {
        System.out.println("\n--- MESEROS DISPONIBLES ---");
        for (Map.Entry<Integer, String> empleado : empleados.entrySet()) {
            System.out.println("ID: " + empleado.getKey() + " -> " + empleado.getValue());
        }
        
        if (empleados.isEmpty()) {
            System.out.println("Error: No hay meseros. Vaya a Gestionar Personal.");
            return;
        }

        int idEmpleado = -1;
        String nombreEmpleado = "";
        
        while (true) {
            System.out.print("\nIngrese su ID de empleado: ");
            String input = scanner.nextLine().trim();
            
            // Función oculta de cancelar
            if (input.equals("0")) {
                System.out.println("Operacion cancelada. Volviendo al menu...");
                return; 
            }
            
            try {
                idEmpleado = Integer.parseInt(input);
                if (empleados.containsKey(idEmpleado)) {
                    nombreEmpleado = empleados.get(idEmpleado);
                    break; 
                } else {
                    System.out.println("Error: ID no encontrado.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: El ID debe ser un numero.");
            }
        }

        String nombreCliente = "";
        while (true) {
            System.out.println("\n--- DATOS DEL CLIENTE ---");
            System.out.println("Ingrese el nombre del cliente:");
            System.out.print("-> ");
            nombreCliente = scanner.nextLine().trim();
            
            // Función oculta de cancelar
            if (nombreCliente.equals("0")) {
                System.out.println("Operacion cancelada. Volviendo al menu...");
                return; 
            }
            
            if (!nombreCliente.isEmpty()) break;
            System.out.println("Error: El nombre no puede estar vacio.");
        }

        List<String> productosDelCliente = new ArrayList<>();
        boolean agregandoProductos = true;

        while (agregandoProductos) {
            System.out.println("\n--- MENU DE PRODUCTOS ---");
            for (String item : menuProductosDisplay) {
                System.out.println(item);
            }
            
            // Único lugar donde el texto 0 - Cancelar pedido es visible
            System.out.println("0 - Cancelar pedido");

            String busquedaLimpia = "";
            
            while (true) {
                System.out.print("\nIngrese el CODIGO o NOMBRE del producto: ");
                String producto = scanner.nextLine().trim();
                
                if (producto.equals("0")) {
                    System.out.println("Operacion cancelada. Volviendo al menu...");
                    return;
                }
                
                if (producto.isEmpty()) {
                    System.out.println("Error: No puede estar vacio.");
                    continue;
                }

                busquedaLimpia = normalizarTexto(producto);
                
                if (menuProductosValidador.contains(busquedaLimpia)) {
                    break; 
                } else {
                    System.out.println("Error: El producto no existe en el menu. Verifique el codigo o nombre.");
                }
            }
            
            String nombreBonito = formatoProductos.get(busquedaLimpia);
            productosDelCliente.add(nombreBonito);

            System.out.println("\n¿Desea agregar otro producto a este pedido?");
            System.out.println("S - Si");
            System.out.println("N - No");
            System.out.print("-> ");
            String continuar = scanner.nextLine().trim().toUpperCase();
            
            // Función oculta de cancelar
            if (continuar.equals("0")) {
                System.out.println("Operacion cancelada. Volviendo al menu...");
                return;
            }
            
            if (!continuar.equals("S")) {
                agregandoProductos = false;
            }
        }

        String detalle = "";
        while (true) {
            System.out.println("\n--- DESTINO DEL PEDIDO ---");
            System.out.println("1 - Consumir en el local (Mesa)");
            System.out.println("2 - Para llevar");
            System.out.print("Seleccione (1 o 2): ");
            
            String opDestino = scanner.nextLine().trim();
            
            // Función oculta de cancelar
            if (opDestino.equals("0")) {
                System.out.println("Operacion cancelada. Volviendo al menu...");
                return;
            }
            
            if (opDestino.equals("1")) {
                while (true) {
                    System.out.println("\nIngrese el numero de mesa (1 al 9)");
                    System.out.print("-> ");
                    String inputMesa = scanner.nextLine().trim();
                    
                    // Función oculta de cancelar
                    if (inputMesa.equals("0")) {
                        System.out.println("Operacion cancelada. Volviendo al menu...");
                        return;
                    }
                    
                    try {
                        int numMesa = Integer.parseInt(inputMesa);
                        if (numMesa >= 1 && numMesa <= 9) {
                            detalle = "Mesa " + numMesa;
                            break; 
                        } else {
                            System.out.println("Error: El local solo tiene 9 mesas.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Ingrese un numero valido.");
                    }
                }
                break; 
            } else if (opDestino.equals("2")) {
                detalle = "Para llevar";
                break;
            } else {
                System.out.println("Error: Seleccione 1 o 2.");
            }
        }

        Pedido nuevoPedido = new Pedido(nombreCliente, productosDelCliente, detalle, nombreEmpleado);
        pedidos.offer(nuevoPedido); 
        
        System.out.println("\n[EXITO] Pedido registrado correctamente en la cola de preparacion.");
    }

    private void atenderSiguientePedido() {
        Pedido pedidoAtendido = pedidos.poll(); 
        
        System.out.println("\n--- ZONA DE PREPARACION ---");
        if (pedidoAtendido != null) {
            System.out.println("Preparando pedido:\n" + pedidoAtendido.toString());
        } else {
            System.out.println("No hay pedidos pendientes en la cola.");
        }
    }

    private void verPedidosPendientes() {
        System.out.println("\n--- PANTALLA DE COCINA: PEDIDOS EN ESPERA ---");
        if (pedidos.isEmpty()) {
            System.out.println("No hay ordenes pendientes.");
        } else {
            int contador = 1;
            for (Pedido p : pedidos) {
                System.out.println("\n[Orden #" + contador + "]");
                System.out.println(p.toString());
                contador++;
            }
        }
    }

    public static void main(String[] args) {
        CoffeeShop miCafeteria = new CoffeeShop();
        miCafeteria.iniciarSistema(); 
    }
}