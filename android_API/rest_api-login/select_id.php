<?php
include 'connection.php';

$user = $_POST['user'];

// $query = mysqli_query($con, "SELECT * FROM usuarios WHERE user = '$user'");

$query = mysqli_query($con, "SELECT U.id_usuarios, U.user, U.pass, R.nombre, C.ncuenta FROM usuarios U, r_usuarios R, cuentas C WHERE U.id_usuarios = R.id_usuarios AND U.id_usuarios = C.id_usuarios AND (U.user = '$user')");

$data = array();
$qry_array = array();
$i = 0;

$total = mysqli_num_rows($query);
while ($row = mysqli_fetch_array($query)) {
  $data['id_usuarios'] = $row['id_usuarios'];
  $data['user'] = $row['user'];
  $data['pass'] = $row['pass'];
  $data['nombre'] = $row['nombre'];
  $data['ncuenta'] = $row['ncuenta'];

  $qry_array[$i] = $data;
  $i++;
}

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Dato cargado';
  $response['total'] = $total;
  $response['data'] = $qry_array;

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al cargar el dato';
}

echo json_encode($response);
?>