<?php
include 'connection.php';

$id_usuarios = $_POST['id_usuarios'];
$nombre = $_POST['nombre'];
$email = $_POST['email'];
$telefono = $_POST['telefono'];
$direccion = $_POST['direccion'];

$response = array();

$query = mysqli_query($con, "INSERT INTO r_usuarios (id_usuarios, nombre, email, telefono, direccion) VALUES ('$id_usuarios','$nombre','$email','$telefono','$direccion')");

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Datos agregado con exito';

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al agregar los datos';
}

echo json_encode($response);
?>
