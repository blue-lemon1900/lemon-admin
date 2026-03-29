package org.lemon.commons.security.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 构建可重复读取 inputStream 的 request
 *
 * @author ruoyi
 */
@Getter
public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 请求体字节数据
     */
    private final byte[] body;

    public RepeatedlyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        request.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        body = IOUtils.toByteArray(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedServletInputStream(new ByteArrayInputStream(this.body));
    }

    /**
     * 自定义 ServletInputStream 实现
     */
    private static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream bais;

        public CachedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int read() throws IOException {
            return bais.read();
        }

        @Override
        public int available() throws IOException {
            return bais.available();
        }

        @Override
        public boolean isFinished() {
            return bais.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("不支持异步读取");
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return bais.read(b, off, len);
        }
    }
}
