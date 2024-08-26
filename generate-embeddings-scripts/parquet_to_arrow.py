import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq

# Read the Parquet file into a DataFrame
parquet_file = '../paths_embeddings_3.parquet'
df = pd.read_parquet(parquet_file)

# Convert the DataFrame to an Arrow Table
table = pa.Table.from_pandas(df)

# Write the Arrow Table to a file
arrow_file = '../paths_embeddings_3.arrow'
with pa.OSFile(arrow_file, 'wb') as sink:
    with pa.RecordBatchFileWriter(sink, table.schema) as writer:
        writer.write_table(table)

print(f"Data successfully written to {arrow_file}")