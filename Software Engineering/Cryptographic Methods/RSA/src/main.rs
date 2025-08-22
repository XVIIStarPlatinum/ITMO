use input::prompt;
use acropolis::{find_private_key, generate_dictionary_block, encrypt};
use city::{decrypt};

fn main() {
    println!("1. Шифрование\n2. Расшифрование");
    let input: String = prompt("Выберите режим: ");
    loop {
        match input.as_str() {
            "1" => {
                process_rsa_encryption();
                break
            },
            "2" => {
                process_rsa_decryption();
                break
            },
            _ => eprintln!("Попробуйте ещё раз.")
        }
    }
}

fn process_rsa_encryption() {
    let p = prompt("Введите p: ").parse::<u64>().unwrap();
    let q = prompt("Введите q: ").parse::<u64>().unwrap();
    let c = prompt("Введите c: ").parse::<u64>().unwrap();
    let private_key = find_private_key(p, q, c);
    println!("d: {}", private_key[0]);
    println!("N: {}", private_key[1]);
    let msg = prompt("Введите шифруемое сообщение: ");
    let msg_ascii = generate_dictionary_block(msg);
    print!("Поток сообщений: ");
    for element in &msg_ascii {
        print!("{}", element);
    }
    let msg_ascii_enc = encrypt(msg_ascii, c, private_key[1]);
    print!("\nЗашифрованное сообщение: {}", msg_ascii_enc);
}

fn process_rsa_decryption() {
    let encrypted_text = prompt("Введите шифрованный текст: ");
    let private_key: Vec<u64> = prompt("Введите закрытый ключ: ")
        .split(|c: char| c == ' ' || c == ',')
        .filter(|s| !s.is_empty())
        .filter_map(|s| s.parse::<u64>().ok())
        .collect();
    println!("Город: {}", decrypt(encrypted_text, private_key));
}
