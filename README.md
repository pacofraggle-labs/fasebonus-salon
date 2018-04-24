Este es un proyecto Java 1.6+

Para ejecutar hay que hacer uso del script run.sh.

No ha habido tiempo de hacer una aplicación compilada en condiciones así que es necesario tener instalado Java 1.6+ y Apache Maven.

run.sh acepta el parámetro --rebuild para recompilar el programa.

La configuración inicial está almacenada en el fichero config.properties

  googlesheet: URL del Google Doc donde se actualizan las puntuaciones
  historico-gid: Pestaña con los records
  puntuaciones-gid: Pestaña con las puntuaciones (para estadísticas de edición)
  avatar-folder: Carpeta con imágenes de participante
  games-folder: Carpeta con imágenes de los juegos
  current-edition: Edición actual
  current-edition-from: Fecha de inicio de la edición actual
  current-edition-to: Fecha de fin de la edición actual
  player-bar: Fichero que configura el aspecto de la barra de firma


En avatar-folder se esperan imágenes cuyo nombre de fichero corresponde con el nick del usuario.
En games-folder se esperan marquesinas de juego que añadir en las tablas de puntuaciones de cada juego de la edición actual. El nombre de fichero debe coincidir con las 18 primeras letras (o hasta el primer paréntesis) del título en minúsculas y sin espacios.

Los resultados se generarán en una carpeta ./tmp


ranking.yaml: Define donde va cada valor en la tabla de records de cada juego
marcador...yaml: Define donde va cada valor en el pie de firma de diamantes
