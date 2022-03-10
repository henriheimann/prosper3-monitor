function decode_int16(byte_high, byte_low, scale) {
  var int16 = ((byte_high << 8) | byte_low);
  if (int16 == 32767) {
      return null;
  } if (int16 >= 32768) {
      return (int16 - 65536) / scale;
  } else {
      return int16 / scale;
  }
}
  
function decode_uint64(byte_3, byte_2, byte_1, byte_0) {
  return byte_3 << 24 | byte_2 << 16 | byte_1 << 8 | byte_0;
}

function decodeUplink(input) {
  var sensor_type = input.bytes[0];
  var battery_voltage = input.bytes[1] / 10;
  var temperature = decode_int16(input.bytes[3], input.bytes[2], 100);
  var humidity = null;
  var ir_temperature = null;
  var brightness_current = null;
  var moisture_counter = null;

  if (sensor_type === 0) {
    humidity = decode_int16(input.bytes[5], input.bytes[4], 100);
    ir_temperature = decode_int16(input.bytes[7], input.bytes[6], 100);
    brightness_current = decode_uint64(input.bytes[11], input.bytes[10], input.bytes[9], input.bytes[8]);
  } else if (sensor_type === 1) {
    moisture_counter = decode_uint64(input.bytes[7], input.bytes[6], input.bytes[5], input.bytes[4]);
  }

  return {
    data: {
      sensor_type: sensor_type,
      battery_voltage: battery_voltage,
      temperature: temperature,
      humidity: humidity,
      ir_temperature: ir_temperature,
      brightness_current: brightness_current,
      moisture_counter: moisture_counter
    },
    warnings: [],
    errors: []
  };
}
