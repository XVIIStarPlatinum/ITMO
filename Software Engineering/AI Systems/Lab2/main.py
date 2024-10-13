from swiplserver import PrologMQI, PrologThread, create_posix_path
import re
from abc import ABC, abstractmethod
import signal
import sys

KNOWLEDGE_BASE = "./Lab1.pl"

class AbstractQueryProcessor(ABC):
    def run_query(self, prolog: PrologThread):
        result = prolog.query(self.form_query())
        if not result or len(result) == 0:
            self.handle_failure(result)
        else:
            self.handle_success(result)

    @abstractmethod
    def form_query(self):
        pass

    @abstractmethod
    def handle_failure(self, result):
        pass

    @abstractmethod
    def handle_success(self, result):
        pass

class CompoundFinder(AbstractQueryProcessor):
    def __init__(self, classification: str):
        self.classification = classification

    def form_query(self):
        return f'chemical_class(X, {self.classification})'

    def handle_success(self, result):
        result = set([line['X'] for line in result])
        print(f'Найден {len(result)} органических соединений с классификацией "{self.classification}":')
        for index, line in enumerate(result, 1):
            print(f'{index}. {line}')

    def handle_failure(self, result):
        print(f'Нет органических соединений с классификацией "{self.classification}".')

class NFPAWarning(AbstractQueryProcessor):
    def __init__(self, nfpa: str):
        self.nfpa = nfpa

    def form_query(self):
        return f'nfpa(X, _, _, _, \'{self.nfpa}\')'

    def handle_success(self, result):
        result = set([line['X'] for line in result])
        print(f'Найден {len(result)} органических соединений с особым предупреждением "{self.nfpa}":')
        for index, line in enumerate(result, 1):
            print(f'{index}. {line}')

    def handle_failure(self, result):
        print(f'Нет органических соединений с особым предупреждением "{self.nfpa}".')

patterns = {
    r'Какие соединения имеют классификацию (.+)\?': CompoundFinder,
    r'Какие соединения имеют особое предупреждение (.+)\?': NFPAWarning
}

def signal_handler(sig, frame):
    print("Ctrl+C?")
    sys.exit(0)

with PrologMQI() as mqi:
    signal.signal(signal.SIGINT, signal_handler)

    with mqi.create_thread() as prolog:
        path = create_posix_path(KNOWLEDGE_BASE)
        print(prolog.query(f'consult("{path}")'))

        while True:
            query = input("> ")
            if query.lower() == "quit" or query.lower() == "q":
                break

            for pattern in patterns:
                match = re.match(pattern, query, re.IGNORECASE)
                if match is None:
                    continue
                processor = patterns[pattern](*match.groups())
                processor.run_query(prolog)
                break
            else:
                print("Неверный запрос.")