use std::io::{self, Write};

pub fn prompt(msg: &str) -> String {
    print!("{}", msg);

    io::stdout().flush().expect("Не получилось очистить stdout");

    let mut s = String::new();
    io::stdin()
        .read_line(&mut s)
        .expect("Ошибка при прочтении значения");
    s.trim().to_string()
}