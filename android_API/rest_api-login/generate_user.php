<?php
include 'connection.php';

$user = $_POST['user'];
$pass = "temporal";

$response = array();

$query = mysqli_query($con, "INSERT INTO usuarios (user, pass) VALUES ('$user','$pass')");

$ultimo_id = mysqli_insert_id($con);

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Usuario generado con exito';
  $response['id_registro'] = '' . $ultimo_id;

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al generar el usuario';
}

echo json_encode($response);
?>
