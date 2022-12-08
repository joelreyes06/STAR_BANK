<?php
include 'connection.php';

$user = $_POST['user'];
$pass = $_POST['pass'];

$query = mysqli_query($con, "UPDATE usuarios SET pass = '$pass' WHERE user = '$user'");

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Dato actualizado con exito';

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al actualizar el dato';
}

echo json_encode($response);
?>
