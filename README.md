<h1>Cómo se envían los archivos desde el cliente</h1>

<p>
    El cliente envía archivos a un server mediante sockets.<br>
    Los envía en objetos que están compuestos así:
</p>

<h3><strong>FilePart: </strong></h3>
<ul>
    <li>String fileName</li>
    <li>int partNumber</li>
    <li>byte[] data</li>
</ul>

<p
    Ver FilePart en el paquete de modelos. <br><br>
    Este FilePart tiene el nombre del archivo, el número de la parte y un array de bytes de tamaño 1500 o menos donde viene la información útil del archivo. <br><br>
    Los métodos para reconstruir este archivo se encuentran en el paquete de com.prog.distribuida.utils, en la clase Utils, el método estático <strong>rebuildAndSaveFile().</strong>
</p>
