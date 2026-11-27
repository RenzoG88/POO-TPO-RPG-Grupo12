# Desarrollo

El proyecto es un RPG por turnos en Java con Swing. El dominio no depende de la interfaz: la vista pide datos y el controlador aplica las acciones sobre el modelo.

Esta guía describe cómo está armado el código y cómo se agrega cada tipo de contenido que el diseño ya contempla.

## Arquitectura

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Modelo | `modelo.*` | Reglas de la partida, el combate, los personajes y el inventario |
| Vista | `vista.*` | Ventanas y paneles Swing |
| Controlador | `controlador` | Traduce la acción de la pantalla en una llamada al modelo |
| DTO | `dto` | Datos planos para dibujar la interfaz |

El punto de entrada de la interfaz es `InicioSwing`. Crea `PantallaInicio` y `ControladorInicio`. `Main` solo demuestra el inventario por consola.

`Partida` es el eje del modelo. Guarda el equipo, la tienda, el ciclo, el estrés y la etapa:

- `PREPARACION`: armar el equipo, comprar y guardar.
- `COMBATE`: hay una `Batalla` activa. La batalla no se serializa.
- `ELECCION_MEJORA`: queda abierta después de ganar a un jefe.
- `FINALIZADA`: derrota, o el equipo completo está despedido.

`SesionPartida` conserva la referencia a la partida en curso. Al cargar un archivo, los controladores pasan a usar esa instancia.

Dentro del modelo hay cinco bloques:

- `partida`: ciclo de juego, encuentros, recompensas y persistencia.
- `combatientes`: `Combatiente`, empleados, enemigos y `Equipo`.
- `combate`: `Batalla`, turnos y habilidades.
- `inventario`: ítems, consumibles, equipamiento y tienda.
- `estadostemporales`: efectos que duran uno o varios turnos.

La forma de extender el juego es agregar una subclase y registrarla en el catálogo que ya recorre esa familia. `Batalla` y los controladores leen habilidades, estados e ítems por polimorfismo, así que un contenido nuevo no necesita una pantalla propia.

## Inicio de una partida

`ControladorInicio.nuevaPartida()` crea una `Partida`. El constructor llama a `iniciarNueva()`, y ese método hace `equipo = new Equipo()`.

Los personajes jugables se instancian en el constructor de `Equipo` (`juego/src/modelo/combatientes/Equipo.java`):

```java
empleados.add(new Ciberseguridad());
empleados.add(new Infraestructura());
empleados.add(new Backend());
empleados.add(new Frontend());
```

Cada subclase de `Empleado` define su nombre, sus estadísticas y su habilidad inicial. El equipo también crea el `Inventario` vacío.

## Catálogo

Las estadísticas de un combatiente se leen en este orden: vida, energía, ataque, defensa y velocidad. En los enemigos, vida, ataque y defensa son la base del ciclo 1. `FabricasEnemigos.escalar` les suma un 15 % por cada ciclo anterior.

### Objetos

La tienda rearma esta lista en cada preparación, en `Tienda.renovarOfertas`. El precio y el bonus de equipo crecen con el ciclo. `progreso` es `ciclo - 1`.

| Objeto | Clase | Efecto en el ciclo 1 | Precio en el ciclo 1 | Imagen |
|---|---|---|---|---|
| Snack | `Snack` | Cura 30 de vida | 20 | `recursos/objetos/snack.png` |
| Cafe | `Cafe` | Recupera 20 de energía | 15 | `recursos/objetos/cafe.png` |
| Teclado mecanico | `Herramienta` | Ataque +10. Después, +2 por ciclo | 50. Después, +10 por ciclo | `recursos/objetos/herramienta.png` |
| Auriculares aislantes | `Proteccion` | Defensa +5. Después, +1 por ciclo | 40. Después, +8 por ciclo | `recursos/objetos/proteccion.png` |

`Snack` y `Cafe` son consumibles: se gastan sobre un empleado. `Herramienta` y `Proteccion` son equipamiento: el empleado tiene un lugar de cada una, y al equipar otra se devuelve la anterior al inventario.

No hay más objetos definidos. Una herramienta o una protección con otro nombre se crea en `renovarOfertas` sin una clase nueva. Un consumible con otro efecto sí necesita su clase, como se describe más abajo.

### Personajes jugables

Salen todos juntos en el constructor de `Equipo`.

| Personaje | Clase | Vida | Energía | Ataque | Defensa | Velocidad | Habilidad |
|---|---|---|---|---|---|---|---|
| Frontend | `Frontend` | 100 | 80 | 22 | 5 | 18 | Doble renderizado: dos golpes al 80 % del ataque, cuesta 10 |
| Backend | `Backend` | 110 | 80 | 28 | 6 | 12 | Proceso pesado: un golpe al 180 % del ataque, cuesta 10 |
| Ciberseguridad | `Ciberseguridad` | 160 | 60 | 18 | 16 | 8 | Firewall activo: un aliado recibe 25 % menos de daño durante 2 turnos, cuesta 10 |
| Infraestructura | `Infraestructura` | 120 | 100 | 12 | 8 | 10 | Reiniciar servicio: cura 30 de vida a un aliado, cuesta 10 |

### Enemigos

| Enemigo | Clase | Vida | Energía | Ataque | Defensa | Velocidad | Comportamiento |
|---|---|---|---|---|---|---|---|
| Usuario phishing | `UsuarioPhishing` | 96 | 0 | 24 | 5 | 9 | Ataca al primer empleado activo |
| Cliente exigente | `ClienteExigente` | 144 | 40 | 22 | 4 | 7 | Alterna un ataque con "Lo necesito para ayer": burnout de 5 de daño durante 3 turnos |
| Burocrata | `Burocrata` | 120 | 30 | 20 | 8 | 6 | Alterna un ataque con "Primero carga un ticket": el objetivo pierde 1 turno |
| Jefe de la empresa | `JefeEmpresa` | 336 | 60 | 35 | 10 | 11 | Alterna "Reunion de directorio" (golpe al 150 %) con "Hay que ponerse la camiseta" (burnout de 5 durante 3 turnos) |

Los tres primeros entran en `FabricasEnemigos.normales()`. El jefe solo aparece cuando el estrés llega a `limiteEstres`.

### Habilidades

| Habilidad | Clase | Qué hace |
|---|---|---|
| Doble renderizado | `AtaquePotenciado` | Multiplicador 0.8, 2 golpes |
| Proceso pesado | `AtaquePotenciado` | Multiplicador 1.8, 1 golpe |
| Reunion de directorio | `AtaquePotenciado` | Multiplicador 1.5, 1 golpe |
| Reiniciar servicio | `Reparacion` | Cura 30 |
| Firewall activo | `AplicarEstado` | `FIREWALL_ACTIVO`, 2 turnos, potencia 25 |
| Lo necesito para ayer | `AplicarEstado` | `BURNOUT`, 3 turnos, potencia 5 |
| Hay que ponerse la camiseta | `AplicarEstado` | `BURNOUT`, 3 turnos, potencia 5 |
| Primero carga un ticket | `AplicarEstado` | `TICKET_PENDIENTE`, 1 turno |

### Estados

| Estado | Clase | Efecto |
|---|---|---|
| Burnout | `Burnout` | Al empezar el turno recibe daño igual a la potencia. El jefe y el cliente usan potencia 5 durante 3 turnos |
| Ticket pendiente | `TicketPendiente` | `permiteActuar` devuelve falso. El burócrata lo aplica por 1 turno |
| Firewall activo | `FirewallActivo` | Reduce el daño recibido en el porcentaje de la potencia. Ciberseguridad usa 25 % durante 2 turnos |

## Personaje jugable

Un empleado nuevo extiende `Empleado`. La referencia es `Frontend`.

El constructor de `Empleado` recibe nombre, vida máxima, energía máxima, ataque, defensa y velocidad. La habilidad se registra con `agregarHabilidad`. `readObject` vuelve a registrarla al cargar un guardado, porque la lista de habilidades puede llegar vacía desde una partida anterior.

```java
public class QA extends Empleado {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoPersonaje obtenerTipoPersonaje() {
        return TipoPersonaje.QA;
    }

    public QA() {
        super("QA", 90, 70, 16, 8, 14);
        registrarHabilidadInicial();
    }

    private void registrarHabilidadInicial() {
        if (obtenerHabilidades().isEmpty()) {
            agregarHabilidad(new Reparacion("Hotfix", 8, 25));
        }
    }

    private void readObject(ObjectInputStream entrada)
            throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        registrarHabilidadInicial();
    }
}
```

Para que forme parte de toda partida nueva, se agrega en el constructor de `Equipo`:

```java
empleados.add(new QA());
```

La identidad visual vive en el enum `TipoPersonaje`. Si la batalla debe mostrar un sprite, el valor nuevo también se asocia a un archivo en `SpritesPersonajes`. Las imágenes están en `juego/src/recursos/personajes/`.

Al subir de nivel, `Empleado.subirNivel()` llama a `mejorarEstadisticas(vida, energia, ataque, defensa)`. El umbral está en `obtenerExperienciaSiguienteNivel()` y hoy es `nivel * 100`. La experiencia de una victoria se reparte solo entre los empleados activos, desde `Equipo.repartirExperiencia`.

## Enemigo

Un rival extiende `Enemigo` e implementa `ejecutarAccion(Equipo)`. `UsuarioPhishing` ataca al primer empleado activo. `ClienteExigente` alterna un ataque con una habilidad.

`elegirObjetivo` devuelve el primer activo del equipo. Otro criterio se resuelve recorriendo `equipo.obtenerActivos()` dentro de `ejecutarAccion`. Este ejemplo elige al empleado con menos vida:

```java
@Override
public void ejecutarAccion(Equipo equipo) {
    if (!puedeActuar()) {
        return;
    }
    Empleado objetivo = null;
    for (Empleado empleado : equipo.obtenerActivos()) {
        if (objetivo == null || empleado.obtenerVida() < objetivo.obtenerVida()) {
            objetivo = empleado;
        }
    }
    if (objetivo != null) {
        atacar(objetivo);
    }
}
```

Los encuentros no conocen las clases concretas. `GeneradorEncuentros` pide enemigos a una lista de `FabricaEnemigo`. El catálogo está en `FabricasEnemigos`:

- `normales()` devuelve las fábricas de los rivales comunes.
- `crearJefe` devuelve el jefe.
- `escalar` sube vida, ataque y defensa un 15 % por cada ciclo anterior al actual.

Un enemigo nuevo declara `VIDA_BASE`, `ATAQUE_BASE` y `DEFENSA_BASE`, expone un método `crearX(int ciclo)` que construye la instancia con `escalar`, y se suma a la lista de `normales()`. El generador lo incluye solo con ese registro. Para reemplazar al jefe se cambia `crearJefe`.

La cantidad de rivales de un encuentro normal está en el `for` de `GeneradorEncuentros.crearEnemigos`. Hoy crea dos.

## Habilidad

`Habilidad` es abstracta. Cada subclase define `objetivoValido` y `aplicar`. El costo de energía lo resuelve la batalla antes de llamar a `aplicar`.

`AtaquePotenciado` es el molde de un golpe: calcula el daño con `obtenerAtaqueTotal()` y `obtenerDefensaTotal()`, y lo aplica con `recibirDanio`. El objetivo es válido si ambos están activos y pertenecen a bandos distintos.

`Reparacion` es el molde de una cura: `curar` entre empleados activos que todavía no están al máximo de vida.

Una habilidad de estado no necesita clase propia. `AplicarEstado` recibe un `TipoEstado`, una duración y una potencia:

```java
agregarHabilidad(new AplicarEstado("Firewall activo", 10,
        TipoEstado.FIREWALL_ACTIVO, 2, 25));
```

Ciberseguridad registra el firewall de esa forma. Cliente exigente registra el burnout y lo usa desde `ejecutarAccion` con `usarHabilidad`.

La habilidad se agrega en el constructor del personaje. La pantalla de batalla lee `obtenerHabilidades()` del combatiente actual.

## Inventario y tienda

`Item` es la raíz. `Consumible` se usa sobre un empleado. `Equipamiento` se coloca en un empleado: `Herramienta` suma ataque y `Proteccion` suma defensa. Esos bonus se aplican en `Empleado.obtenerAtaqueTotal` y `obtenerDefensaTotal`.

Un consumible nuevo copia `Snack` (vida) o `Cafe` (energía). Implementa `puedeAplicarse` y `aplicar`. Un empleado despedido no es un objetivo válido. `Snack` llama a `curar` y `Cafe` a `recuperarEnergia`.

El catálogo que se ofrece en cada preparación está en `Tienda.renovarOfertas`. Ahí se arma la lista de `Oferta` con el ítem y el precio. Una herramienta o una protección nueva puede crearse directo en ese método:

```java
nuevas.add(new Oferta(new Analgesico(), 25));
nuevas.add(new Oferta(new Herramienta("Monitor", 8), 45));
```

`Partida.comprar` solo acepta la compra en `PREPARACION` y delega en `Tienda.comprar`. Usar o equipar pasa por `Equipo` y el inventario.

El inventario nace con 0 créditos, en el constructor de `Inventario`. Los créditos entran por `agregarCreditos`, al cobrar una recompensa, y salen por `gastarCreditos`. Si el precio supera el saldo, la compra no se hace. Dar créditos al empezar la partida es asignar otro valor en ese constructor.

`ControladorObjetos.crearDatos` arma el texto y la imagen de cada ítem con `instanceof`. Un consumible con descripción propia se agrega en ese método. Las imágenes de objetos están en `juego/src/recursos/objetos/`.

## Estados temporales

`EfectoEstado` dura una cantidad de turnos. Al cerrar el turno se descuenta la duración y, al llegar a cero, el gestor lo descarta. Al terminar el combate, `Partida.limpiarEstadosDelCombate` los saca de empleados y enemigos.

El efecto se elige sobrescribiendo un gancho:

| Comportamiento | Método | Referencia |
|---|---|---|
| Daño al empezar el turno | `alIniciarTurno` | `Burnout` |
| El personaje no actúa | `permiteActuar` devuelve `false` | `TicketPendiente` |
| Recibe menos daño | `modificarDanioRecibido` | `FirewallActivo` |

Si el personaje ya tiene un estado del mismo `TipoEstado`, `GestorEstados.agregar` no crea otro. Conserva el efecto que estaba, con su potencia, y le suma la duración nueva con `sumarDuracion`. Un segundo burnout de potencia distinta no reemplaza al primero ni acumula el daño: solo alarga los turnos que faltan. Cambiar ese criterio es cambiar el bucle de `agregar`.

`TipoEstado` fabrica la instancia. Una constante nueva implementa `crearEfecto(duracion, potencia)` y devuelve la clase correspondiente. `AplicarEstado` usa esa fábrica y no referencia las clases concretas.

El objetivo válido de `AplicarEstado` depende del tipo. `FIREWALL_ACTIVO` va de empleado a empleado. El resto va de enemigo a empleado. Un estado ofensivo nuevo entra por esa segunda rama. Un beneficio nuevo para aliados necesita su propio caso en `objetivoValido`.

## Reglas del combate

El daño, la defensa, el orden de los turnos, el gasto de energía y el fin del combate ya están resueltos. Un cambio de esas reglas se hace en el método que las calcula. `Batalla` comprueba el turno, escribe el registro, cierra el turno y decide si el combate terminó. La recompensa la entrega `Partida`.

### Daño y defensa

`Combatiente.atacar` calcula el golpe así:

```java
int danio = Math.max(1, obtenerAtaqueTotal() - objetivo.obtenerDefensaTotal());
objetivo.recibirDanio(danio);
```

El mínimo de un ataque es 1, aunque la defensa supere al ataque. `obtenerAtaqueTotal` y `obtenerDefensaTotal` incluyen el equipo en los empleados.

`recibirDanio` aplica después dos reducciones, en este orden:

1. Si el personaje está defendiendo, el daño se divide por 2 y el resultado mínimo vuelve a ser 1.
2. `GestorEstados.modificarDanio` deja actuar a los estados, por ejemplo el firewall.

`defender` marca `defendiendo`. Esa marca se apaga al empezar su próximo turno, en `iniciarTurno`, antes de procesar los estados. Cambiar el 50 % de la defensa es cambiar la división en `recibirDanio`. Cambiar la fórmula del golpe es cambiar `atacar`.

### Orden de turnos

`GestorTurnos.iniciarRonda` arma la ronda con los participantes que siguen activos y los ordena por `obtenerVelocidad`, de mayor a menor. El ordenamiento es estable: si dos tienen la misma velocidad, queda primero quien ya estaba antes. Al armar la lista, los empleados entran antes que los enemigos, así que un empate los deja primeros.

`Batalla.prepararTurno` llama a `iniciarTurno`. Si `puedeActuar` es falso, anota que pierde el turno, llama a `finalizarTurno` y avanza. Un enemigo juega solo en `ejecutarTurnoEnemigo`, que delega en `ejecutarAccion`.

### Energía

`Combatiente.usarHabilidad` exige que el personaje pueda actuar, que la habilidad le pertenezca y que el objetivo sea válido. Después llama a `gastarEnergia` con el costo. Si la energía no alcanza, devuelve falso y la habilidad no se aplica. El ataque básico no gasta energía.

En la pantalla, `ControladorBatalla` avisa "No alcanza la energía" y el turno sigue disponible.

### Objeto durante el combate

`Batalla.usarItem` solo corre en el turno de un empleado. Delega en `Equipo.usarConsumible` y, si el uso fue válido, cierra el turno. El consumible se gasta en el inventario. Entre combates, el mismo objeto se usa por `ControladorConsumibles` y no consume un turno.

### Fin del combate

Un combatiente está activo mientras `estaActivo` ve vida mayor que 0. En un empleado, vida 0 es estar despedido.

`Batalla.estaFinalizada` es verdadera si la batalla ya empezó y se cumple alguna de estas dos condiciones: el equipo está entero despedido, o no queda ningún enemigo activo. `fueVictoria` pide que esté finalizada y que todavía haya algún empleado activo. Si los dos bandos quedan en 0 al mismo tiempo, cuenta como derrota.

`Partida.procesarFinCombate` es quien reparte experiencia y créditos, y quien pasa a `ELECCION_MEJORA`, a preparación o a `FINALIZADA`. La batalla solo informa el desenlace.

## Pantalla

Un dato nuevo del modelo se muestra en dos pasos: el controlador lo lee y el panel lo escribe. El modelo no conoce Swing.

| Qué hay que cambiar | Dónde |
|---|---|
| Ciclo, estrés y créditos del equipo | `ControladorEquipo.refrescarVista` llama a `PantallaEquipo.mostrarProgreso` y `mostrarEquipo` |
| Texto del botón de avanzar | El mismo `refrescarVista` arma la acción y llama a `mostrarEtapa` |
| Mensaje de error o de confirmación | `mostrarMensaje` de la pantalla que está abierta |
| Ficha de un empleado o un enemigo | `PanelEmpleado` y `PanelEnemigo` |
| Texto del turno, el registro y las barras | `PanelBatalla`, alimentado por `ControladorBatalla` |
| Nombre, precio e imagen de un objeto | `ControladorObjetos.crearDatos` y `PanelObjetos` |

El estrés ya se ve en la pantalla de equipo como `Estres: actual/limite`. `PantallaEquipo.mostrarProgreso` es el método que arma esa etiqueta. Un texto nuevo de esa barra se cambia ahí, y el valor se sigue pasando desde `ControladorEquipo`.

El recorrido de ventanas está en los controladores:

| Paso | Quién abre la ventana |
|---|---|
| Menú inicial | `InicioSwing` muestra `PantallaInicio` |
| Equipo | `ControladorInicio.abrirPartida` crea `PantallaEquipo` y cierra el menú |
| Tienda o inventario | `ControladorPartida.abrirTienda` y `mostrarInventario` crean `ControladorObjetos` |
| Batalla | `ControladorPartida.iniciarCombate` crea `PantallaBatalla` y `ControladorBatalla` |
| Resultado | Al cerrar la batalla, `ControladorBatalla` hace `dispose` si `estaFinalizada`. `ControladorPartida` llama a `procesarFinCombate` y abre `PantallaResultadoBatalla` |
| Vuelta al equipo | `elegirMejora` cierra el resultado y `ControladorEquipo.refrescarVista` actualiza la pantalla que seguía abierta |

Cambiar a qué pantalla se vuelve después de ganar o perder es cambiar `ControladorPartida.iniciarCombate` y `mostrarResultado`.

## Reglas de la partida

Estos valores viven en el modelo y definen el balance:

| Regla | Ubicación | Valor actual |
|---|---|---|
| Estrés que convierte el próximo combate en jefe | `Partida.limiteEstres` | 5 |
| Recompensa de un combate normal | `Partida.iniciarSiguienteCombate` | 40 de experiencia y 30 créditos |
| Recompensa de un jefe | mismo método | 100 de experiencia y 80 créditos |
| Mejora al vencer al jefe | `Partida.elegirMejora` | +0.20 al multiplicador de experiencia o de créditos |
| Avance de ciclo | `elegirMejora` | suma 1 al ciclo y vuelve el estrés a 0 |
| Precios y ofertas | `Tienda.renovarOfertas` | se regeneran al entrar en preparación |
| Créditos al crear el inventario | `Inventario` | 0 |

`elegirMejora` solo corre en la etapa `ELECCION_MEJORA`. Después de un combate normal el estrés sube uno y la partida vuelve a preparación. Una derrota, o un equipo sin empleados activos, pasa a `FINALIZADA`.

El guardado está en `PersistenciaPartida`. Se puede guardar en preparación y en la elección de mejora. La batalla en curso es `transient` y no entra en el archivo.

## Ejecución

La interfaz se compila y se publica con Docker Compose, desde la raíz del repositorio:

```bash
docker compose up -d --build
```

El código se compila dentro de la imagen, así que un cambio de fuentes pide ese mismo comando. La partida queda en http://127.0.0.1:6080 y la clase de arranque es `InicioSwing`.
