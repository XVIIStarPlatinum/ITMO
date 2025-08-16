mod vigenere;

use std::io::{self, Write};

fn prompt(msg: &str) -> String {
    print!("{}", msg);

    io::stdout().flush().expect("Не получилось очистить stdout");

    let mut s = String::new();
    io::stdin()
        .read_line(&mut s)
        .expect("Ошибка при прочтении значения");
    s.trim().to_string()
}

fn main() {
    let mut input = prompt("Введите сообщение: ");
    let key = prompt("Введите ключ: ");

    println!("1. Зашифровать\n2. Расшифровать");
    let mode = prompt("Введите режим: ");
    
    match mode.as_str() { 
        "1" => input = vigenere::encode(&input, &key),
        "2" => input = vigenere::decode(&input, &key),
        _ => {}
    }
    println!("Ответ: {}", input.trim());
}
