package edu.masanz.da.ta.dao;

import edu.masanz.da.ta.conf.Ini;
import edu.masanz.da.ta.dto.*;
import edu.masanz.da.ta.utils.Security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static edu.masanz.da.ta.conf.Ctes.*;
import static edu.masanz.da.ta.conf.Ini.*;

/**
 * Clase que simula la capa de acceso a datos. Cuando veamos las interfaces crearemos una interfaz para esta clase.
 * También crearemos una clase que implemente esa interfaz y que se conecte a una base de datos relacional.
 * Y una clase servicio que podrá utilizar cualquiera de las dos implementaciones, la simulada, la real u otra.
 * Por ahora, simplemente es una clase con métodos estáticos que simulan la interacción con una base de datos.
 */
public class Dao {


    //region Colecciones que simulan la base de datos
    private static Map<String, Usuario> mapaUsuarios;

    private static Map<Long, Item> mapaItems;

    private static Map<Long, List<Puja>> mapaPujas;
    //endregion

    //region Inicialización de la base de datos simulada
    public static void ini() {
        iniMapaUsuarios();
        iniMapaItems();
        iniMapaPujas();
    }

    private static void iniMapaUsuarios() {
        mapaUsuarios = new HashMap<>();
        for (int i = 0; i < USUARIOS.length; i++) {
            String[] partes = USUARIOS[i].split(",");
            mapaUsuarios.put(partes[0], new Usuario(partes[0], partes[1], partes[2], partes[3]));
        }
    }

    private static void iniMapaItems() {
        mapaItems = new HashMap<>();
        for (int i = 0; i < ITEMS.length; i++) {
            String[] partes = ITEMS[i].split(",");
            mapaItems.put(Long.parseLong(partes[0]),
                    new Item(Long.parseLong(partes[0]), partes[1], partes[2], Integer.parseInt(partes[3]),
                            partes[4], partes[5], Integer.parseInt(partes[6]), Boolean.getBoolean(partes[7])));
        }
    }

    private static void iniMapaPujas() {
        // TODO 03 iniMapaPujas
        mapaPujas = new HashMap<>();
        for (int i = 0; i < PUJAS.length; i++) {
            String[] partes = PUJAS[i].split(",");
            mapaPujas.put(Long.parseLong(partes[0]), new ArrayList<>());
            mapaPujas.get(Long.parseLong(partes[0])).add(new Puja(Long.parseLong(partes[0]), partes[1], Integer.parseInt(partes[2]), partes[3]));
        }
    }
    //endregion

    //region Usuarios
    public static boolean autenticar(String nombreUsuario, String password) {
//        return password.equals("1234"); - ??????? ??????????????????
        // TODO 04 autenticar
        if (mapaUsuarios.containsKey(nombreUsuario)){
            try {
                MessageDigest hash = MessageDigest.getInstance("SHA-1");
                byte[] hashBytes = hash.digest((mapaUsuarios.get(nombreUsuario).getSal() + password).getBytes());
                StringBuilder hashFinal = new StringBuilder();
                for (int i = 0; i < hashBytes.length; i++) {
                    hashFinal.append(String.format("%02x", hashBytes[i]));
                }
                if (hashFinal.toString().equals(mapaUsuarios.get(nombreUsuario).getHashPwSal())){
                    return true;
                }
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean esAdmin(String nombreUsuario) {
//        return nombreUsuario.equalsIgnoreCase("Admin");
        // TODO 05 esAdmin
        return mapaUsuarios.get(nombreUsuario).getRol().equalsIgnoreCase("Admin");
    }

    public static List<Usuario> obtenerUsuarios() {
        // TODO 06 obtenerUsuarios
        List<Usuario> lista = new ArrayList<>();
        mapaUsuarios.forEach((clave, usuario) -> lista.add(usuario));
        return lista;
    }

    public static boolean crearUsuario(String nombre, String password, boolean esAdmin) {
        // TODO 07 crearUsuario
        if (mapaUsuarios.containsKey(nombre)) {
            return false;
        }

        try {
            byte[] salt2 = new byte[16];
            new SecureRandom().nextBytes(salt2);

            String salt = Base64.getEncoder().encodeToString(salt2);

            MessageDigest hash = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = hash.digest((salt + password).getBytes());
            StringBuilder hashFinal = new StringBuilder();
            for (int i = 0; i < hashBytes.length; i++) {
                hashFinal.append(String.format("%02x", hashBytes[i]));
            }
            Usuario nuevoUsuario;
            if (esAdmin){
                nuevoUsuario = new Usuario(nombre, hashFinal.toString(), salt, "Admin");
            } else{
                nuevoUsuario = new Usuario(nombre, hashFinal.toString(), salt, "Usuario");
            }


            // Agregar el nuevo usuario al mapa
            mapaUsuarios.put(nombre, nuevoUsuario);
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean modificarPasswordUsuario(String nombre, String password) {
        // TODO 08 modificarPasswordUsuario
        return false;
    }

    public static boolean modificarRolUsuario(String nombre, String rol) {
        mapaUsuarios.get(nombre).setRol(rol);
        return true;
    }

    public static boolean eliminarUsuario(String nombre) {
        // TODO 10 eliminarUsuario
        mapaUsuarios.remove(nombre);
        return true;
    }

    //endregion

    //region Validación de artículos
    public static List<Item> obtenerArticulosPendientes() {
        List<Item> lista = new ArrayList<>();
        mapaItems.forEach((id, item) -> {
            if (item.getEstado() == 0){
                lista.add(item);
            }
        });
        return lista;
    }

    public static boolean validarArticulo(long id, boolean valido) {
        // TODO 12 validarArticulo
        mapaItems.get(id).setEstado(1);
        valido  = true;
        return valido;
    }

    public static boolean validarTodos() {
        mapaItems.forEach((id, item) -> item.setEstado(1));
        return true;
    }
    //endregion

    //region Gestión de artículos y pujas de administrador
    public static List<ItemPujas> obtenerArticulosConPujas() {
        // TODO 14 obtenerArticulosConPujas
        return null;
    }

    public static boolean resetearSubasta() {
        // TODO 15 resetearSubasta
        return true;
    }

    public static List<PujaItem> obtenerHistoricoGanadores() {
        // TODO 16 obtenerHistoricoGanadores
        return null;
    }
    //endregion

    //region Acciones por parte de usuario normal (no admin)

    public static Item obtenerArticuloPujable(long idArt) {
        // TODO 17 obtenerArticuloPujable
        return null;
    }

    public static List<Item> obtenerArticulosPujables() {
        // TODO 18 obtenerArticulosPujables
        return null;
    }

    public static boolean pujarArticulo(long idArt, String nombre, int precio) {
        // TODO 19 pujarArticulo
        return false;
    }

    public static List<PujaItem> obtenerPujasVigentesUsuario(String nombreUsuario) {
        // TODO 20 obtenerPujasVigentesUsuario
        return null;
    }

    public static boolean ofrecerArticulo(Item item) {
        // TODO 21 ofrecerArticulo
        return true;
    }

    //endregion
}
