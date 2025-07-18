# Трехуровневый кеш для хранения изображений


## Цель проекта:
Создать кеш для хранения изображений, состоящий из трёх уровней с различными стратегиями хранения и обработки ссылок. 
Каждый уровень демонстрирует разные типы ссылок в Java:

1. Strong Cache (Уровень 1):
Уровень, использующий стандартные сильные ссылки, сохраняет часто используемые изображения в оперативной памяти.
Размер этого уровня ограничен фиксированным количеством слотов.
По достижении лимита новых элементов старые вытесняются согласно алгоритму LRU (Least Recently Used) - недавно использованные элементы сохраняются, наименее востребованные удаляются первыми.
2. Soft Cache (Уровень 2):
Уровень, применяющий мягкие ссылки (SoftReference), сохраняющие изображения, которые были вытеснены из первого уровня.
Мягкие ссылки позволяют сохранять ресурсы в условиях нехватки памяти. Если Java ощущает нехватку ресурсов, мягкие ссылки будут освобождаться сборщиком мусора, обеспечивая возможность освободить дополнительную оперативную память.
Этот уровень предназначен для кратковременного сохранения промежуточных результатов, которые потенциально могут снова понадобиться, но не настолько критичны, как первый уровень.
3. Weak Cache (Уровень 3):
Третий уровень основан на слабых ссылках (WeakReference), используемых для долговременного хранения редкопотребляемых данных.
Эти ссылки позволят сохранить некоторые крупные объекты (изображения), если свободные ресурсы имеются, но при первой возможности сборщик мусора сможет удалить их без влияния на производительность системы.
Основная цель третьего уровня - долгосрочное сохранение объектов, которые почти наверняка будут нужны крайне редко.

## Функционал программы:
Реализовать загрузку изображений.
Добавлять новые изображения в кэш последовательно начиная с первого уровня и далее, перемещаясь по уровням в зависимости от доступности места и активности объектов.
Предоставить методы для извлечения изображений из любого уровня кеша с поддержкой автоматического перемещения активированных изображений обратно вверх (например, переупорядочивание активного объекта в Level 1, если оно было найдено на нижних уровнях).
Отобразить статистику использования каждого уровня (количество хранимых объектов, частоту попадания/промаха).
Если объект недоступен ни на одном уровне, загрузить изображение из базы (для простоты достаточно будет сымитировать загрузку из базы).


### Дополнительные задания (опционально):
Автоматическое масштабирование размеров кэшей (Level 1–3) в зависимости от доступной оперативной памяти.
Возможность конфигурируемого ограничения количества хранимых объектов на каждом уровне.
Визуализация структуры хранилища и статистики работы (графики промахов и попаданий).