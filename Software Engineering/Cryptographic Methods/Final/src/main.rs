use csv::Reader;
use image::ImageReader;
use input::prompt;
use num::bigint::{BigUint};
use once_cell::sync::Lazy;
use std::collections::HashMap;
use std::error::Error;

const CYR_DICTIONARY: Lazy<HashMap<char, u8>> = Lazy::new(|| HashMap::from([
    ('А', 192), ('Б', 193), ('В', 194), ('Г', 195), ('Д', 196), ('Е', 197), ('Ж', 198), ('З', 199),
    ('И', 200), ('Й', 201), ('К', 202), ('Л', 203), ('М', 204), ('Н', 205), ('О', 206), ('П', 207),
    ('Р', 208), ('С', 209), ('Т', 210), ('У', 211), ('Ф', 212), ('Х', 213), ('Ц', 214), ('Ч', 215),
    ('Ш', 216), ('Щ', 217), ('Ъ', 218), ('Ы', 219), ('Ь', 220), ('Э', 221), ('Ю', 222), ('Я', 223),
    ('а', 224), ('б', 225), ('в', 226), ('г', 227), ('д', 228), ('е', 229), ('ж', 230), ('з', 231),
    ('и', 232), ('й', 233), ('к', 234), ('л', 235), ('м', 236), ('н', 237), ('о', 238), ('п', 239),
    ('р', 240), ('с', 241), ('т', 242), ('у', 243), ('ф', 244), ('х', 245), ('ц', 246), ('ч', 247),
    ('ш', 248), ('щ', 249), ('ъ', 250), ('ы', 251), ('ь', 252), ('э', 253), ('ю', 254), ('я', 255),
    (' ', 32), ('\n', 10)
]));
const CYR_DICTIONARY_INVERTED: Lazy<HashMap<u8, char>> = Lazy::new(||CYR_DICTIONARY.iter()
    .map(|(k, v)| (*v, *k)).collect());

fn main() {
    println!("1. От изображений\n2. От CSV");
    let input: String = prompt("Выберите режим: ");
    let encrypted_data_int: Vec<u8>;
    let encrypted_data_rem: Vec<u8>;
    loop {
        match input.as_str() {
            "1" => {
                let (file_path_int, file_path_rem) = (
                    "data/pics/array_int_parts_4.png",
                    "data/pics/array_remainders_4.png",
                );
                (encrypted_data_int, encrypted_data_rem) = {
                    let a = image_to_array(file_path_int);
                    let b = image_to_array(file_path_rem);
                    (a.unwrap().into_iter().flatten().collect::<Vec<u8>>(), b.unwrap().into_iter().flatten().collect())
                };
                break;
            }
            "2" => {
                let (file_path_int, file_path_rem) = (
                    "data/csv/array_int_parts_4.csv",
                    "data/csv/array_remainders_4.csv");
                (encrypted_data_int, encrypted_data_rem) = {
                    let a = csv_to_array(file_path_int);
                    let b = csv_to_array(file_path_rem);
                    (a.into_iter().flatten().collect(), b.into_iter().flatten().collect())
                };
                break
            }
            _ => eprintln!("Попробуйте ещё раз."),
        }
    }
    let (d, p, q) = {
        let private_exp = prompt("Введите d: ").parse::<u64>().unwrap();
        let p = prompt("Введите p: ").parse::<u64>().unwrap();
        let q = prompt("Введите q: ").parse::<u64>().unwrap();
        (private_exp, p, q)
    };
    let encrypted_data: Vec<u64> = encrypted_data_int.iter()
        .zip(encrypted_data_rem.iter())
        .map(|(&elem1, &elem2)| ((elem1 as u64) << 8) | (elem2 as u64))
        .collect::<Vec<u64>>();
    println!("{}", decrypt(encrypted_data, [d, p, q]));
}

pub fn decrypt(encrypted: Vec<u64>, private_key: [u64; 3]) -> String {
    let n = private_key[1] * private_key[2];
    let mut msg: String = "".to_string();
    for c in encrypted {
        let m: BigUint = BigUint::modpow(&BigUint::from(c), &BigUint::from(private_key[0]), &BigUint::from(n));
        msg.push(CYR_DICTIONARY_INVERTED[&m.to_string().parse::<u8>().unwrap()]);
    }
    msg
}
fn image_to_array(path: &str) -> Result<Vec<Vec<u8>>, Box<dyn Error>> {
    let img = ImageReader::open(path).unwrap().decode().unwrap();
    let gray = img.to_luma8();
    let (width, _) = gray.dimensions();
    let pixels = gray.into_raw();

    let w = width as usize;
    if pixels.len() % w != 0 {
        return Err("Несоответствие длины/ширины изображений".into());
    }

    let rows: Vec<Vec<u8>> = pixels.chunks_exact(w).map(|chunk| chunk.to_vec()).collect();

    Ok(rows)
}

fn csv_to_array(path: &str) -> Vec<Vec<u8>> {
    let mut csv_data = Reader::from_path(path).unwrap();
    let mut result_vector: Vec<Vec<u8>> = Vec::new();
    for res in csv_data.records() {
        let record = res.unwrap();
        for field in record.iter() {
            match field.parse::<u8>() {
                Ok(num) => result_vector.push(num.to_be_bytes().to_vec()),
                Err(e) => eprintln!("Ошибка при парсинге значений '{}': {}", field, e),
            }
        }
    }
    result_vector
}
