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

    
    
    let mut to_encode = false;
    loop {
        println!("1. Зашифровать\n2. Расшифровать");
        let mode = prompt("Введите режим: ");
        match mode.as_str() {
            "1" => {
                to_encode = true;
                break
            },
            "2" => {
                break
            },
            _ => println!("Неправильный режим, попробуйте снова")
        }
    }
    input = vigenere::encode(&input, &key, to_encode);
    println!("Ответ: {}", input.trim());
}
