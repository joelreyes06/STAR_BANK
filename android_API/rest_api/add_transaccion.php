<?php
include 'connection.php';

$id_usuario_Emisor = $_POST['id_usuario_Emisor'];
$cuenta_Receptor = $_POST['cuenta_Receptor'];
$cantidad = $_POST['cantidad'];
$fecha = $_POST['fecha'];
$descripcion = $_POST['descripcion'];

$response = array();

$query = mysqli_query($con, "INSERT INTO transacciones (id_usuario_Emisor, cuenta_Receptor, cantidad, fecha, descripcion) VALUES ('$id_usuario_Emisor','$cuenta_Receptor','$cantidad','$fecha','$descripcion')");

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Transaccion creada con exito';

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al crear la transaccion';
}

echo json_encode($response);
?>
