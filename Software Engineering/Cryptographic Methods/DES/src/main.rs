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

/**
 * c = 1111000001
 * k = 00110
 * Сперва делим шифротекст на левую и правую части:
 * l_0=11110
 * r_0=00001
 * Тогда, согласно алгоритму
 * l_1 = r_0 = 00001,
 * r_1 = l_0 ^ k_1 ^ r_0 = 11110 ^ 00110 ^ 00001 = 11001.
 * Объединяя l_1 и r_1, получим
 * C = 0000111001
 * Правильный ответ: 0000111001
 */
fn main() {
    let mut plain_text: String = prompt("Введите открытый текст: ");
    let mut keys: Vec<u16> = vec![];
    let rounds: i32 = prompt("Введите количество раундов: ").parse().expect("Это не число.");
    let text_len = plain_text.len();

    for _ in 0..rounds {
        let key = u16::from_str_radix(&*prompt("Введите ключ: "), 2).expect("Invalid binary string");
        keys.push(key);
    }

    let mid_idx: usize = text_len / 2;

    for i in 0..rounds {
        let (left, right) = plain_text.split_at(mid_idx);
        let b_left = u16::from_str_radix(left, 2).expect("Invalid binary string");
        let b_right = u16::from_str_radix(right, 2).expect("Invalid binary string");

        let left_next = b_right;
        let right_next = b_left ^ keys[i as usize] ^ b_right;
        let mut left_1: String = format!("{:b}", left_next);
        let mut right_1: String = format!("{:b}", right_next);
        while left_1.len() < mid_idx {
            left_1 = format!("{}{}", 0, left_1);
        }
        while right_1.len() < mid_idx {
            right_1 = format!("{}{}", 0, right_1);
        }
        plain_text = format!("{}{}", left_1, right_1);
        println!("Сообщение после {} раунда: {}", i + 1, plain_text);
    }
    println!("Ответ: {}", plain_text);
}
