use std::collections::HashMap;
use modpow::modpow;
use num::bigint::{BigInt, BigUint};

pub fn decrypt(encrypted: String, private_key: Vec<u64>) -> String {
    let chars: Vec<char> = encrypted.chars().collect();
    let enc_vec: Vec<u64> = chars.chunks(4)
        .map(|chunk| chunk.iter().collect::<String>())
        .map(|s| s.parse::<u64>().unwrap()).collect::<Vec<u64>>();
    let mut msg: String = "".to_string();
    for c in enc_vec {
        let m: BigInt = modpow(&BigUint::from(c), &BigUint::from(private_key[0]), &BigUint::from(private_key[1]));
        msg.push(get_dictionary_char(m.to_string().parse::<u8>().unwrap()));
    }
    msg
}

fn get_dictionary_char(c: u8) -> char {
    let cyr_dictionary: HashMap<u8, char> = HashMap::from([
        (192, 'А'), (193, 'Б'), (194, 'В'), (195, 'Г'), (196, 'Д'), (197, 'Е'), (198, 'Ж'), (199, 'З'),
        (200, 'И'), (201, 'Й'), (202, 'К'), (203, 'Л'), (204, 'М'), (205, 'Н'), (206, 'О'), (207, 'П'),
        (208, 'Р'), (209, 'С'), (210, 'Т'), (211, 'У'), (212, 'Ф'), (213, 'Х'), (214, 'Ц'), (215, 'Ч'),
        (216, 'Ш'), (217, 'Щ'), (218, 'Ъ'), (219, 'Ы'), (220, 'Ь'), (221, 'Э'), (222, 'Ю'), (223, 'Я'),
        (224, 'а'), (225, 'б'), (226, 'в'), (227, 'г'), (228, 'д'), (229, 'е'), (230, 'ж'), (231, 'з'),
        (232, 'и'), (233, 'й'), (234, 'к'), (235, 'л'), (236, 'м'), (237, 'н'), (238, 'о'), (239, 'п'),
        (240, 'р'), (241, 'с'), (242, 'т'), (243, 'у'), (244, 'ф'), (245, 'х'), (246, 'ц'), (247, 'ч'),
        (248, 'ш'), (249, 'щ'), (250, 'ъ'), (251, 'ы'), (252, 'ь'), (253, 'э'), (254, 'ю'), (255, 'я')
    ]);
    cyr_dictionary[&c]
}