El cliente envía archivos a un server mediante sockets.

Los envía en objetos que están compuestos así:

FilePart: 
    String fileName
    int partNumber
    byte[] data

Ver FilePart en el paquete de modelos.

Este FilePart tiene el nombre del archivo, el número de la parte y un array de bytes de tamaño 1500 o menos donde viene la información útil del archivo.

Los métodos para reconstruir este archivo se encuentran en el paquete de com.prog.distribuida.utils, en la clase Utils, el método estático rebuildAndSaveFile().