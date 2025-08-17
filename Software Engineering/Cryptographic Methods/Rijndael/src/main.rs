use std::io::{self, Write};

mod rijndael;

fn prompt(msg: &str) -> String {
    print!("{}", msg);

    io::stdout().flush().expect("Не получилось очистить stdout");

    let mut s = String::new();
    io::stdin()
        .read_line(&mut s)
        .expect("Ошибка при прочтении значения");
    s.trim().to_string()
}

fn print_matrix(block: &Vec<Vec<u8>>) {
    for row in block {
        println!("{}", row.iter()
            .map(|b| format!("{:02x}", b))
            .collect::<Vec<_>>()
            .join(" "));
    }
}
/** https://en.wikipedia.org/wiki/Advanced_Encryption_Standard#Optimization_of_the_cipher
 * Пример ввода:
    19 a0 9a e9
    3d f4 c6 f8
    e3 e2 8d 48
    be 2b 2a 08

    a0 88 23 2a
    fa 54 a3 6c
    fe 2c 39 76
    17 b1 39 05
*/
 
fn main() {
    let mut plain_text: Vec<Vec<u8>> = vec![];
    let mut round_key: Vec<Vec<u8>> = vec![];
    for _ in 0..4 {
        let line = prompt("Введите одну строку открытого текста: ");

        if line.trim().is_empty() {
            break;
        }
        
        let inner_vec: Vec<u8> = line
            .split_whitespace()
            .filter_map(|s| u8::from_str_radix(s, 16).ok())
            .collect();

        plain_text.push(inner_vec);
    }
    for _ in 0..4 {
        let line = prompt("Введите одну строку ключевого блока: ");

        if line.trim().is_empty() {
            break;
        }

        let inner_vec: Vec<u8> = line
            .split_whitespace()
            .filter_map(|s| u8::from_str_radix(s, 16).ok())
            .collect();

        round_key.push(inner_vec);
    }
    
    plain_text = rijndael::sub_bytes(plain_text);
    println!("Блок после SubBytes: ");
    print_matrix(&plain_text);
    
    plain_text = rijndael::shift_rows(plain_text);
    println!("Блок после ShiftRowa: ");
    print_matrix(&plain_text);

    plain_text = rijndael::mix_columns(plain_text);
    println!("Блок после MixColumns: ");
    print_matrix(&plain_text);

    plain_text = rijndael::add_round_key(plain_text, round_key);
    println!("Блок после AddRoundKey: ");
    print_matrix(&plain_text);
}
