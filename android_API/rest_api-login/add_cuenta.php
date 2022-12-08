<?php
include 'connection.php';

$id_usuarios = $_POST['id_usuarios'];
$ncuenta = $_POST['ncuenta'];
$fecha = $_POST['fecha'];
$cantidad = $_POST['cantidad'];

$response = array();

$query = mysqli_query($con, "INSERT INTO cuentas (id_usuarios, ncuenta, fecha, cantidad) VALUES ('$id_usuarios','$ncuenta','$fecha','$cantidad')");

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Cuenta creada con exito';

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al crear la cuenta';
}

echo json_encode($response);
?>
