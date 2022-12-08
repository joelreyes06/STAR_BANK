<?php
include 'connection.php';
$query = mysqli_query($con, "SELECT * FROM usuarios");
$data = array();
$qry_array = array();
$i = 0;
$total = mysqli_num_rows($query);

while ($row = mysqli_fetch_array($query)) {
  $data['id_usuarios'] = $row['id_usuarios'];
  $data['user'] = $row['user'];
  $data['pass'] = $row['pass'];

  $qry_array[$i] = $data;
  $i++;
}

if($query) {
  $response['success'] = 'true';
  $response['message'] = 'Datos cargados con exito';
  $response['total'] = $total;
  $response['data'] = $qry_array;

} else {
  $response['success'] = 'false';
  $response['message'] = 'Error al cargar los datos';
}

echo json_encode($response);
?>
